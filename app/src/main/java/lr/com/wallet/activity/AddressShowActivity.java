package lr.com.wallet.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import lr.com.wallet.R;
import lr.com.wallet.dao.WalletDao;
import lr.com.wallet.pojo.ETHWallet;
import lr.com.wallet.utils.AddressEncoder;
import lr.com.wallet.utils.ZXingUtils;

/**
 * Created by dt0814 on 2018/7/21.
 */

public class AddressShowActivity extends Activity implements View.OnClickListener {
    private ETHWallet ethWallet;
    private TextView addressText;
    private ImageButton addressPreBut;
    private ImageView addressQr;
    private Button copyAddressButton;
    private ClipData mClipData;
    private ClipboardManager clipManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_show_layout);
        ethWallet = WalletDao.getCurrentWallet();
        addressText = findViewById(R.id.addressText);
        addressPreBut = findViewById(R.id.addressPreBut);
        addressPreBut.setOnClickListener(this);
        addressText.setText(ethWallet.getAddress());
        addressQr = findViewById(R.id.addressQr);
        copyAddressButton = findViewById(R.id.copyAddressButton);
        copyAddressButton.setOnClickListener(this);
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        addressQr.measure(width, height);
        height = addressQr.getMeasuredHeight();
        width = addressQr.getMeasuredWidth();
        AddressEncoder addressEncoder = new AddressEncoder(ethWallet.getAddress());
        Bitmap qrImage = ZXingUtils.createQRImage(AddressEncoder.encodeERC(addressEncoder), width, height);
        addressQr.setImageBitmap(qrImage);
        clipManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.addressPreBut:
                AddressShowActivity.this.finish();
                break;
            case R.id.copyAddressButton:
                mClipData = ClipData.newPlainText("Label", ethWallet.getAddress());
                clipManager.setPrimaryClip(mClipData);
                Toast.makeText(AddressShowActivity.this, "钱包收款地址复制成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}