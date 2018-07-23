package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import lr.com.wallet.R;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.Md5Utils;
import lr.com.wallet.utils.Web3jUtil;


/**
 * Created by dt0814 on 2018/7/16.
 */

public class TxActivity extends Activity {
    private EditText toAddress;
    private EditText TNum;
    private TextView txPrice;
    private Button TranscationBut;
    private Context context;
    private View orderView;
    private ETHWallet wallet;
    private BigInteger gase = new BigInteger("21000");
    private BigInteger gasPrice = new BigInteger("1000000000");
    private String pwd = "";
    private SeekBar seekBar;
    private LayoutInflater inflater;
    private String costNum;
    private ImageButton saoyisao;
    private ImageButton trxPreBut;
    private AlertDialog alertDialog;
    private AlertDialog mDialog;
    private static final int NOT_NOTICE = 2;//如果勾选了不再询问

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tx_layout);
        toAddress = findViewById(R.id.toAddress);
        TNum = findViewById(R.id.TNum);
        TranscationBut = findViewById(R.id.TranscationBut);
        context = getBaseContext();
        txPrice = findViewById(R.id.txPrice);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(10000);
        seekBar.setMin(1);
        inflater = getLayoutInflater();
        trxPreBut = findViewById(R.id.trxPreBut);
        saoyisao = findViewById(R.id.saoyisao);
        saoyisao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(TxActivity.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //先判断有没有权限 ，没有就在这里进行权限的申请
                    ActivityCompat.requestPermissions(TxActivity.this,
                            new String[]{android.Manifest.permission.CAMERA}, 1);
                } else {
                    startActivityForResult(new Intent(TxActivity.this, CaptureActivity.class), 0);
                }

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                System.out.println("onProgressChanged");
                int progress = seekBar.getProgress();
                BigDecimal bigDecimal = Convert.toWei(progress / 100 + "", Convert.Unit.GWEI);
                BigDecimal bigDecimal1 = new BigDecimal(gase.toString());
                gasPrice = bigDecimal.toBigInteger();
                bigDecimal = bigDecimal.multiply(bigDecimal1);
                BigDecimal eth = Convert.fromWei(bigDecimal, Convert.Unit.ETHER);
                costNum = eth.toString();
                txPrice.setText(costNum + "\b\beth");
                System.out.println(progress + "___________" + gasPrice);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println("onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println("onStopTrackingTouch");
            }
        });

        trxPreBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TxActivity.this.finish();
            }
        });
        TranscationBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toAddress.getText().toString().trim().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TxActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("转账地址不呢为空");
                    builder.setPositiveButton("是", null);
                    builder.show();
                }
                String content = TNum.getText().toString().trim();

                String pattern = "[0-9]+([.]{1}[0-9]+){0,1}";

                boolean isMatch = Pattern.matches(pattern, content);
                AlertDialog.Builder alertbBuilder = new AlertDialog.Builder(TxActivity.this);
                if (!isMatch) {
                    alertbBuilder.setTitle("提示");
                    alertbBuilder.setMessage("金额只能是数字");
                    alertbBuilder.setPositiveButton("是", null);
                    alertbBuilder.show();
                    return;
                }

                alertbBuilder = new AlertDialog.Builder(TxActivity.this);
                orderView = inflater.inflate(R.layout.order_layout, null);
                TextView toAddressMassage = orderView.findViewById(R.id.toAddressMassage);
                TextView payAddressMassage = orderView.findViewById(R.id.payAddressMassage);
                TextView costMassage = orderView.findViewById(R.id.costMassage);
                TextView numMassage = orderView.findViewById(R.id.numMassage);
                toAddressMassage.setText(toAddress.getText());
                payAddressMassage.setText(wallet.getAddress());
                costMassage.setText(costNum);
                numMassage.setText(TNum.getText() + "\b\beth");
                alertbBuilder.setView(orderView);
                alertbBuilder.setTitle("支付详情").setMessage("").setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(TxActivity.this);
                                View pwdView = inflater.inflate(R.layout.input_pwd_layout, null);
                                builder.setView(pwdView);
                                builder.setTitle("输入密码").setMessage("").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        EditText text = pwdView.findViewById(R.id.inPwdBut);
                                        pwd = text.getText().toString();
                                        if (!wallet.getPassword().equals(Md5Utils.md5(pwd))) {
                                            text.setText("");
                                            Toast.makeText(TxActivity.this, "密码错误重新输入", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        try {
                                            System.out.println(gasPrice + "___________" + gase);
                                            Web3jUtil.ethTransaction(wallet.getAddress(), toAddress.getText().toString(),
                                                    ETHWalletUtils.derivePrivateKey(wallet, pwd), gasPrice, gase,
                                                    TNum.getText().toString());
                                            Toast.makeText(TxActivity.this, "成功发起转账请耐心等待", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(TxActivity.this, MainFragmentActivity.class));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                builder.show();


                                dialog.cancel();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
                alertbBuilder.show();

            }
        });
        toAddress.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                } else {
                    // 此处为失去焦点时的处理内容
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                wallet = WalletDao.getCurrentWallet();
                                gase = Web3jUtil.getEstimateGas(wallet.getAddress(), toAddress.getText().toString().trim());
                                gasPrice = Web3jUtil.getGasPrice();
                                BigDecimal bigDecimal = Convert.fromWei(gasPrice.toString(), Convert.Unit.GWEI);
                                BigInteger bigInteger = bigDecimal.toBigInteger();
                                Integer integer = new Integer(bigInteger.toString());
                                seekBar.setProgress(integer * 100);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString("result");
            toAddress.setText(result);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    startActivityForResult(new Intent(TxActivity.this, CaptureActivity.class), 0);
                } else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    Toast.makeText(TxActivity.this, "请手动打开相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }
}
