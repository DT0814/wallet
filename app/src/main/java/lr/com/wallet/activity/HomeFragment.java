package lr.com.wallet.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lr.com.wallet.R;
import lr.com.wallet.adapter.TransactionAdapter;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.pojo.TransactionBean;
import lr.com.wallet.pojo.TransactionPojo;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.Md5Utils;
import lr.com.wallet.utils.TransactionUtils;
import lr.com.wallet.utils.Web3jUtil;
import lr.com.wallet.utils.ZXingUtils;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private FragmentActivity activity;
    private Context context;
    private ETHWallet ethWallet;
    private LayoutInflater inflater;
    private ClipboardManager clipManager;
    private ClipData mClipData;
    private ImageView imageView;
    private TextView ethNum;
    private View view;
    private AlertDialog.Builder alertbBuilder;
    private Button copyPrvKey;
    private Button copyKeyStore;
    private Button copyWalletAddress;
    private Button sendTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.home_fragment, null);
        super.onCreate(savedInstanceState);
        ethNum = view.findViewById(R.id.ethNum);
        imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.qr_code);
        imageView.setOnClickListener(this);
        activity = getActivity();
        context = inflater.getContext();
        ethWallet = WalletDao.getCurrentWallet();
        if (null == ethWallet) {
            startActivity(new Intent(activity, CreateWalletActivity.class));
            return null;
        }
        Handler qrHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                imageView.measure(width, height);
                height = imageView.getMeasuredHeight();
                width = imageView.getMeasuredWidth();
                Bitmap qrImage = ZXingUtils.createQRImage(ethWallet.getAddress(), width, height);
                imageView.setImageBitmap(qrImage);
            }
        };

        qrHandler.sendMessage(new Message());
        clipManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);

        copyPrvKey = view.findViewById(R.id.copyPrvKey);
        copyKeyStore = view.findViewById(R.id.copyKeyStore);
        copyWalletAddress = view.findViewById(R.id.copyWalletAddress);
        sendTransaction = view.findViewById(R.id.sendTransaction);

        copyKeyStore.setOnClickListener(this);
        copyPrvKey.setOnClickListener(this);
        copyWalletAddress.setOnClickListener(this);
        sendTransaction.setOnClickListener(this);

        alertbBuilder = new AlertDialog.Builder(activity);
        Handler ethNumHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String model = (String) msg.obj;
                ethNum.setText(model);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = Web3jUtil.ethGetBalance(ethWallet.getAddress());
                    Message ms = new Message();
                    ms.obj = "ETH: " + s;
                    ethNumHandler.sendMessage(ms);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        initTransactionListView();
        return view;
    }

    private void initTransactionListView() {
        ListView listView = (ListView) view.findViewById(R.id.transcationList);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                listView.setAdapter((ListAdapter) msg.obj);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        TransactionBean itemAtPosition = (TransactionBean) adapterView.getItemAtPosition(i);

                        Intent intent = new Intent(activity, TxInfoActivity.class);
                        intent.putExtra("obj", JsonUtils.objectToJson(itemAtPosition));
                        startActivity(intent);

                    }
                });
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<TransactionBean> list = new ArrayList();
                    TransactionPojo pojo;
                    pojo = TransactionUtils.getTransactionPojo(ethWallet.getAddress());
                    TransactionAdapter adapter = new TransactionAdapter(activity, R.layout.tx_list_view, pojo.getResult(), ethWallet);
                    Message msg = new Message();
                    msg.obj = adapter;

                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    @Override
    public void onClick(View view) {

        View pwdView;
        switch (view.getId()) {
            case R.id.copyPrvKey:
                pwdView = inflater.inflate(R.layout.input_pwd_layout, null);
                alertbBuilder.setView(pwdView);
                alertbBuilder.setTitle("请输入密码").setMessage("").setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = pwdView.findViewById(R.id.inPwdBut);
                                String pwd = editText.getText().toString();
                                String privateKey = ETHWalletUtils.derivePrivateKey(ethWallet, pwd);
                                if (null == privateKey || privateKey.equals("")) {
                                    Toast.makeText(activity, "密码错误请重新输入", Toast.LENGTH_SHORT).show();
                                } else {
                                    mClipData = ClipData.newPlainText("Label", privateKey);
                                    clipManager.setPrimaryClip(mClipData);
                                    Toast.makeText(activity, "私钥已经复制到剪切板", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }

                }).create();
                alertbBuilder.show();
                break;
            case R.id.copyWalletAddress:
                mClipData = ClipData.newPlainText("Label", ethWallet.getAddress());
                clipManager.setPrimaryClip(mClipData);
                Toast.makeText(activity, "复制成功", Toast.LENGTH_SHORT).show();

                System.out.println("copyWalletAddress");
                break;
            case R.id.copyKeyStore:

                pwdView = inflater.inflate(R.layout.input_pwd_layout, null);
                alertbBuilder.setView(pwdView);
                alertbBuilder.setTitle("请输入密码").setMessage("").setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = pwdView.findViewById(R.id.inPwdBut);
                                String pwd = editText.getText().toString();
                                if (!ethWallet.getPassword().equals(Md5Utils.md5(pwd))) {
                                    editText.setText("");
                                    Toast.makeText(activity, "密码错误重新输入", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                String keyStoreStr = ETHWalletUtils.deriveKeystore(ethWallet);
                                mClipData = ClipData.newPlainText("Label", keyStoreStr);
                                clipManager.setPrimaryClip(mClipData);
                                Toast.makeText(activity, "keyStore复制到剪切板", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
                alertbBuilder.show();
                break;
            case R.id.sendTransaction:
                startActivity(new Intent(activity, TxActivity.class));
                break;
            case R.id.imageView:
                View QrView = inflater.inflate(R.layout.qr_image_layout, null);
                ImageView image = QrView.findViewById(R.id.qr_image);
                Bitmap qrImage = ZXingUtils.createQRImage(ethWallet.getAddress(), 300, 300);
                image.setImageBitmap(qrImage);
                alertbBuilder.setView(QrView);
                alertbBuilder.setPositiveButton("关闭",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertbBuilder.show();
                break;
        }
    }
}
