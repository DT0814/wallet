package lr.com.wallet.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
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
    private EditText gaseNum;
    private Button TranscationBut;
    private Context context;
    private View orderView;
    private String costNum;
    private ETHWallet wallet;
    private BigInteger gase;
    private BigInteger gasPrice;
    private String pwd = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tx_layout);
        toAddress = findViewById(R.id.toAddress);
        TNum = findViewById(R.id.TNum);
        gaseNum = findViewById(R.id.gaseNum);
        TranscationBut = findViewById(R.id.TranscationBut);
        context = getBaseContext();

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String model = (String) msg.obj;
                gaseNum.setText(model);
            }
        };
        LayoutInflater inflater = getLayoutInflater();
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
                TextView orderMassage = orderView.findViewById(R.id.orderMassage);
                TextView toAddressMassage = orderView.findViewById(R.id.toAddressMassage);
                TextView payAddressMassage = orderView.findViewById(R.id.payAddressMassage);
                TextView costMassage = orderView.findViewById(R.id.costMassage);
                TextView numMassage = orderView.findViewById(R.id.numMassage);
                orderMassage.setText("订单信息\b\b 转账");
                toAddressMassage.setText("转入地址\b\b" + toAddress.getText());
                payAddressMassage.setText("付款钱包\b\b" + wallet.getAddress());
                costMassage.setText("旷工费\b\b" + costNum);
                numMassage.setText("金额\b\b" + TNum.getText());
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
                                            Web3jUtil.ethTransaction(wallet.getAddress(), toAddress.getText().toString(),
                                                    ETHWalletUtils.derivePrivateKey(wallet, pwd), gasPrice, gase,
                                                    TNum.getText().toString());
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

                                BigInteger multiply = gasPrice.multiply(gase);
                                BigDecimal bigDecimal = Convert.fromWei(multiply.toString(), Convert.Unit.ETHER);
                                costNum = bigDecimal.toString();
                                System.out.println("costNum::::::::::::::::::" + costNum);
                                Message ms = new Message();
                                ms.obj = gase.toString();
                                handler.sendMessage(ms);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            }
        });
    }
}
