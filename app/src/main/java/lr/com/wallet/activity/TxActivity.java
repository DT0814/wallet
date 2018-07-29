package lr.com.wallet.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import lr.com.wallet.R;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.CoinUtils;
import lr.com.wallet.utils.ETHWalletUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.Md5Utils;
import lr.com.wallet.utils.Web3jUtil;


/**
 * Created by dt0814 on 2018/7/16.
 */

public class TxActivity extends Activity implements View.OnClickListener {
    private EditText toAddress;
    private EditText TNum;
    private TextView txPrice;
    private Button TranscationBut;
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
    CoinPojo coin;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tx_layout);
        toAddress = findViewById(R.id.toAddress);
        TNum = findViewById(R.id.TNum);
        TranscationBut = findViewById(R.id.TranscationBut);
        txPrice = findViewById(R.id.txPrice);

        inflater = getLayoutInflater();
        trxPreBut = findViewById(R.id.trxPreBut);
        trxPreBut.setOnClickListener(this);
        saoyisao = findViewById(R.id.saoyisao);
        saoyisao.setOnClickListener(this);
        Intent intent = getIntent();
        String coinJson = intent.getStringExtra("coin");
        coin = JsonUtils.jsonToPojo(coinJson, CoinPojo.class);
        initSeekBar();
        TranscationBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String to = toAddress.getText().toString().trim();
                if (to.equals("") || to.length() != 42) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TxActivity.this);
                    builder.setTitle("提示");
                    builder.setMessage("转账地址为0x开头的42位字符串");
                    builder.setPositiveButton("是", null);
                    builder.show();
                    return;
                }
                String num = TNum.getText().toString().trim();
                String pattern = "[0-9]+([.]{1}[0-9]+){0,1}";
                boolean isMatch = Pattern.matches(pattern, num);
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

                costMassage.setText(calculationCostNum(gase, gasPrice));

                numMassage.setText(num + "\b\b" + coin.getCoinSymbolName());
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
                                            if (null == gase) {
                                                gase = new BigInteger("21000");
                                            }
                                            if (null == gasPrice) {
                                                gasPrice = new BigInteger("1000000000");
                                            }
                                            String tNum = TNum.getText().toString();
                                            if (coin.getCoinSymbolName().equalsIgnoreCase("ETH")) {
                                                double walletNum = wallet.getNum().doubleValue();
                                                if (walletNum < Double.parseDouble(num) || wallet.getNum().doubleValue() <= 0) {
                                                    Toast.makeText(TxActivity.this, "账户余额不足", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                Web3jUtil.ethTransaction(wallet.getAddress(),
                                                        to,
                                                        ETHWalletUtils.derivePrivateKey(wallet, pwd),
                                                        gasPrice,
                                                        gase,
                                                        TNum.getText().toString());
                                            } else {
                                                double walletNum = wallet.getNum().doubleValue();
                                                if (wallet.getNum().doubleValue() <= 0) {
                                                    Toast.makeText(TxActivity.this, "ETH余额不足", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                if (Double.parseDouble(coin.getCoinCount()) < Double.parseDouble(num)) {
                                                    Toast.makeText(TxActivity.this, coin.getCoinSymbolName() + "余额不足", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        CoinUtils.transaction(wallet.getAddress(),
                                                                to,
                                                                coin.getCoinAddress(),
                                                                gasPrice, gase,
                                                                ETHWalletUtils.derivePrivateKey(wallet, pwd),
                                                                tNum);

                                                    }
                                                }).start();
                                            }
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
                                if (gase != null) {
                                    seekBar.setProgress(Integer.parseInt(gase.toString()));
                                } else {
                                    seekBar.setProgress(21000);
                                }
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        txPrice.setText(calculationCostNum(gase, gasPrice) + "\b\beth");
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initSeekBar() {
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(121000);
        seekBar.setMin(21000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = seekBar.getProgress();
                gase = new BigInteger(progress + "");
                txPrice.setText(calculationCostNum(gase, gasPrice) + "\b\beth");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private String calculationCostNum(BigInteger gase, BigInteger gasePrice) {
        if (null == gase) {
            gase = new BigInteger("21000");
        }
        if (null == gasePrice) {
            gasePrice = new BigInteger("1000000000");
        }
        BigDecimal bigDecimal1 = new BigDecimal(gase.toString());
        BigDecimal bigDecimal = new BigDecimal(gasePrice);
        bigDecimal = bigDecimal.multiply(bigDecimal1);
        BigDecimal eth = Convert.fromWei(bigDecimal, Convert.Unit.ETHER);
        return eth.toString();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saoyisao:
                if (ContextCompat.checkSelfPermission(TxActivity.this,
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //先判断有没有权限 ，没有就在这里进行权限的申请
                    ActivityCompat.requestPermissions(TxActivity.this,
                            new String[]{android.Manifest.permission.CAMERA}, 1);
                } else {
                    startActivityForResult(new Intent(TxActivity.this, CaptureActivity.class), 0);
                }
                break;
            case R.id.trxPreBut:
                TxActivity.this.finish();
                break;
        }
    }
}
