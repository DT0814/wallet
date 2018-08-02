package lr.com.wallet.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.utils.Convert;

import java.math.BigDecimal;

import lr.com.wallet.R;
import lr.com.wallet.pojo.CoinPojo;
import lr.com.wallet.pojo.TxBean;
import lr.com.wallet.utils.DateUtils;
import lr.com.wallet.utils.JsonUtils;
import lr.com.wallet.utils.NumberUtils;
import lr.com.wallet.utils.URLUtils;
import lr.com.wallet.utils.ZXingUtils;

/**
 * Created by dt0814 on 2018/7/17.
 */

public class TxInfoActivity extends FragmentActivity {
    private ImageButton txInfoPreBut;
    private TextView txInfoEthNum;
    private TextView txNumSymName;
    private TextView txInfoFrom;
    private TextView txInfoTo;
    private TextView txInfoGas;
    private TextView txInfoHash;
    private TextView txInfoBlock;
    private TextView txInfoTime;
    private ImageView txInfoQr;
    private Button txInfoUrlBut;
    private ClipboardManager clipManager;
    private ClipData mClipData;
    private AlertDialog.Builder alertbBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tx_info_layout);
        String txBean = getIntent().getStringExtra("txBean");
        String coinJson = getIntent().getStringExtra("coin");
        CoinPojo coinPojo = JsonUtils.jsonToPojo(coinJson, CoinPojo.class);
        TxBean bean = JsonUtils.jsonToPojo(txBean, TxBean.class);
        clipManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        txInfoPreBut = findViewById(R.id.txInfoPreBut);
        txInfoPreBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TxInfoActivity.this.finish();
            }
        });
        txInfoEthNum = findViewById(R.id.txInfoEthNum);
        txNumSymName = findViewById(R.id.txNumSymName);
        if (coinPojo.getCoinSymbolName().equalsIgnoreCase("eth")) {
            txInfoEthNum.setText(Convert.fromWei(bean.getValue(), Convert.Unit.ETHER).toString());
        } else {
            txNumSymName.setText(coinPojo.getCoinSymbolName());
            if (null != bean.getInput()) {
                String value = bean.getInput().substring(74, bean.getInput().length());
                txInfoEthNum.setText(NumberUtils.trnNumber(value).toString());
            } else {
                txInfoEthNum.setText(bean.getValue());
            }
        }


        txInfoFrom = findViewById(R.id.txInfoFrom);
        txInfoFrom.setText(bean.getFrom());

        txInfoTo = findViewById(R.id.txInfoTo);
        txInfoTo.setText(bean.getTo());

        txInfoGas = findViewById(R.id.txInfoGas);
        Long gasPrice = new Long(bean.getGasPrice());
        if (null != bean.getGasUsed()) {
            Long gasUsed = new Long(bean.getGasUsed());
            BigDecimal GasEth = Convert.fromWei(gasPrice * gasUsed + "", Convert.Unit.ETHER);
            txInfoGas.setText(GasEth.toString());
        }


        txInfoHash = findViewById(R.id.txInfoHash);
        txInfoHash.setText(bean.getHash());

        txInfoBlock = findViewById(R.id.txInfoBlock);
        if (null != bean.getBlockNumber()) {
            txInfoBlock.setText(bean.getBlockNumber());
        }

        txInfoTime = findViewById(R.id.txInfoTime);
        Long l = new Long(bean.getTimeStamp()) * 1000;
        txInfoTime.setText(DateUtils.getDateFormatByString(l));

        alertbBuilder = new AlertDialog.Builder(this);

        txInfoQr = findViewById(R.id.txInfoQr);
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        txInfoQr.measure(width, height);
        height = txInfoQr.getMeasuredHeight();
        width = txInfoQr.getMeasuredWidth();
        String url = URLUtils.getTxUrl(bean.getHash());
        Bitmap qrImage = ZXingUtils.createQRImage(url, width, height);
        txInfoQr.setImageBitmap(qrImage);
        txInfoQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = TxInfoActivity.this.getLayoutInflater();
                View QrView = inflater.inflate(R.layout.qr_image_layout, null);
                ImageView image = QrView.findViewById(R.id.qr_image);
                Bitmap qrImage = ZXingUtils.createQRImage(url, 300, 300);
                image.setImageBitmap(qrImage);
                alertbBuilder.setView(QrView);
                alertbBuilder.setPositiveButton("关闭",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertbBuilder.show();
            }
        });


        txInfoUrlBut = findViewById(R.id.txInfoUrlBut);
        txInfoUrlBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClipData = ClipData.newPlainText("Label", url);
                clipManager.setPrimaryClip(mClipData);
                Toast.makeText(TxInfoActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
