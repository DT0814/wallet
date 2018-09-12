package lr.com.wallet.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hunter.wallet.service.SecurityErrorException;
import com.hunter.wallet.service.SecurityUtils;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.regex.Pattern;

import lr.com.wallet.R;
import lr.com.wallet.activity.fragment.info.ContactsActivity;
import lr.com.wallet.dao.CacheWalletDao;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.ETHCacheWallet;
import lr.com.wallet.pojo.TxBean;
import lr.com.wallet.utils.AddressEncoder;
import lr.com.wallet.utils.CoinUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.Type;
import lr.com.wallet.utils.UnfinishedTxPool;
import lr.com.wallet.utils.Web3jUtil;


/**
 * Created by dt0814 on 2018/7/16.
 */

public class TxActivity extends Activity implements View.OnClickListener {
    //收款地址
    private EditText toAddress;
    //交易金额
    private EditText TNum;
    //旷工费用
    private TextView txPrice;
    //当前用户使用钱包
    private ETHCacheWallet wallet;
    //耗费gas用量
    private BigInteger gas;
    //gas价格
    private BigInteger gasPrice = new BigInteger("1000000000");
    //gas用量
    private SeekBar seekBar;
    private LayoutInflater inflater;
    private CoinPojo coin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tx_layout);
        toAddress = findViewById(R.id.toAddress);
        TNum = findViewById(R.id.TNum);
        Button transcationBut = findViewById(R.id.TranscationBut);
        transcationBut.setOnClickListener(this);
        txPrice = findViewById(R.id.txPrice);
        inflater = getLayoutInflater();
        ImageButton trxPreBut = findViewById(R.id.trxPreBut);
        trxPreBut.setOnClickListener(this);
        ImageButton saoyisao = findViewById(R.id.saoyisao);
        saoyisao.setOnClickListener(this);
        ImageButton selectContacts = findViewById(R.id.selectContacts);
        selectContacts.setOnClickListener(this);
        Intent intent = getIntent();
        String coinJson = intent.getStringExtra("coin");
        coin = JsonUtils.jsonToPojo(coinJson, CoinPojo.class);
        if (coin.getCoinSymbolName().equalsIgnoreCase("eth")) {
            gas = new BigInteger("25200");
        } else {
            gas = new BigInteger("60000");
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                txPrice.setText(calculationCostNum(gas, gasPrice) + "\b\beth");
            }
        });
        initSeekBar();

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
                                wallet = CacheWalletDao.getCurrentWallet();
                                gasPrice = Web3jUtil.getGasPrice();
                                if (gasPrice != null) {
                                    Log.i("gasPrice", gasPrice.toString());
                                    BigInteger divide = gasPrice.divide(new BigInteger("1000000000")).multiply(new BigInteger("100"));
                                    seekBar.setProgress(Integer.parseInt(divide.toString()));
                                } else {
                                    seekBar.setProgress(100);
                                }
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        txPrice.setText(calculationCostNum(gas, gasPrice) + "\b\beth");
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


    private void initSeekBar() {
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(100 * 100);
        // seekBar.setMin(21000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress = seekBar.getProgress();
                Log.i("progress", progress + "");
                Double big = (progress / 100d) * 1000000000;
                Log.i("big", big + "");
                gasPrice = new BigInteger(big.longValue() + "");
                Log.i("gasPrice", gasPrice + "");
                txPrice.setText(calculationCostNum(gas, gasPrice) + "\b\beth");
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
        Log.i("CostethNum", eth.toString());
        Log.i("bigDecimalethNum", bigDecimal.toString());
        return eth.toString();
    }

    /**
     * 扫过二维码回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString("result");
            try {
                if (result.startsWith("0x") || result.startsWith("0X")) {
                    toAddress.setText(result);
                } else if (result.startsWith("iban:XE") || result.startsWith("IBAN:XE")) {
                    toAddress.setText(AddressEncoder.decodeICAP(result).getAddress());
                } else if (result.startsWith("iban:") || result.startsWith("IBAN:")) {
                    toAddress.setText(AddressEncoder.decodeLegacyLunary(result).getAddress());
                } else if (result.startsWith("ethereum:") || result.startsWith("ETHEREUM:")) {
                    toAddress.setText(AddressEncoder.decodeERC(result).getAddress());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(TxActivity.this, "二维码解析失败", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == Type.CONSTACT_RESULT_CODE && null != data) {
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
            case R.id.selectContacts:
                Intent intent = new Intent(TxActivity.this, ContactsActivity.class);
                intent.putExtra("itemClickAble", true);
                startActivityForResult(intent, Type.CONSTACT_RESULT_CODE);
                break;
            case R.id.saoyisao:
                //判断摄像头权限
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
            case R.id.TranscationBut:
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
                View orderView = inflater.inflate(R.layout.tx_order_layout, null);
                TextView toAddressMassage = orderView.findViewById(R.id.toAddressMassage);
                TextView payAddressMassage = orderView.findViewById(R.id.payAddressMassage);
                TextView costMassage = orderView.findViewById(R.id.costMassage);
                TextView numMassage = orderView.findViewById(R.id.numMassage);
                toAddressMassage.setText(toAddress.getText());
                payAddressMassage.setText(wallet.getAddress());
                costMassage.setText(calculationCostNum(gas, gasPrice));
                numMassage.setText(num + "\b\b" + coin.getCoinSymbolName());
                alertbBuilder.setView(orderView);

                AlertDialog show = alertbBuilder.show();
                orderView.findViewById(R.id.closeBut).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show.dismiss();
                    }
                });
                orderView.findViewById(R.id.payBut).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(TxActivity.this);
                        View pwdView = inflater.inflate(R.layout.input_pwd_layout, null);
                        builder.setView(pwdView);
                        AlertDialog dialog = builder.show();
                        pwdView.findViewById(R.id.closeBut).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        pwdView.findViewById(R.id.inputPassBut).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText text = pwdView.findViewById(R.id.inPwdEdit);
                                String pwd = text.getText().toString();
                                try {
                                    if (null == gas) {
                                        gas = new BigInteger("21000");
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
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String hash = null;
                                                try {
                                                    hash = Web3jUtil.ethTransaction(wallet.getAddress(),
                                                            to,
                                                            gasPrice,
                                                            gas,
                                                            tNum, wallet, pwd);
                                                    TxBean tx = new TxBean();
                                                    tx.setTimeStamp(new Date().getTime() / 1000 + "");
                                                    tx.setFrom(wallet.getAddress());
                                                    tx.setTo(to);
                                                    tx.setGas(gas.toString());
                                                    tx.setGasPrice(gasPrice.toString());
                                                    tx.setHash(hash);
                                                    tx.setValue(tNum);
                                                    tx.setStatus("");
                                                    UnfinishedTxPool.addUnfinishedTx(tx, coin.getCoinId().toString());
                                                    System.out.println(gas.toString() + "gase消耗数量");
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(TxActivity.this, "交易发起成功", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                } catch (SecurityErrorException e) {
                                                    e.printStackTrace();
                                                    if (e.getErrorCode() == SecurityErrorException.ERROR_PASSWORD_WRONG) {
                                                        Toast.makeText(TxActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                                                        return;
                                                    } else {
                                                        return;
                                                    }
                                                }


                                            }
                                        }).start();

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
                                                String hash = null;
                                                try {
                                                    hash = CoinUtils.transaction(wallet.getAddress(),
                                                            to,
                                                            coin.getCoinAddress(),
                                                            gasPrice, gas,
                                                            Numeric.toHexString(SecurityUtils.getPrikey(wallet.getId().intValue(), pwd)),
                                                            tNum);
                                                } catch (SecurityErrorException e) {
                                                    e.printStackTrace();
                                                }
                                                if (hash == null) {
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(TxActivity.this, "交易请求失败请提高旷工费用", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                    return;
                                                }
                                                TxBean tx = new TxBean();
                                                tx.setTimeStamp(new Date().getTime() / 1000 + "");
                                                tx.setFrom(wallet.getAddress());
                                                tx.setTo(to);
                                                tx.setGas(gas.toString());
                                                tx.setGasPrice(gasPrice.toString());
                                                tx.setHash(hash);
                                                tx.setValue(tNum);
                                                tx.setStatus("");
                                                UnfinishedTxPool.addUnfinishedTx(tx, coin.getCoinId().toString());
                                                System.out.println(gas.toString() + "gase消耗数量");
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(TxActivity.this, "交易发起成功", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                            }
                                        }).start();
                                    }

                                    Toast.makeText(TxActivity.this, "转账发起中", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(TxActivity.this, MainFragmentActivity.class));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

                alertbBuilder.setTitle("支付详情").setMessage("").setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create();

                break;
        }
    }
}
