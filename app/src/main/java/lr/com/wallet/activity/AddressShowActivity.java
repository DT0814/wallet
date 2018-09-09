package lr.com.wallet.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
    private ClipboardManager clipManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_show_layout);
        //查询当前使用的钱包
        ethWallet = WalletDao.getCurrentWallet();
        TextView addressText = findViewById(R.id.addressText);
        ImageButton addressPreBut = findViewById(R.id.addressPreBut);
        addressPreBut.setOnClickListener(this);
        addressText.setText(ethWallet.getAddress());
        ImageView addressQr = findViewById(R.id.addressQr);
        Button copyAddressButton = findViewById(R.id.copyAddressButton);
        copyAddressButton.setOnClickListener(this);
        addressQr.post(new Runnable() {
            @Override
            public void run() {
                Log.i("addressQr", addressQr.getWidth() + "  " + addressQr.getHeight());
                Bitmap qrImage = ZXingUtils.createQRImage(AddressEncoder.encodeERC(new AddressEncoder(ethWallet.getAddress())), addressQr.getWidth(), addressQr.getHeight());
                addressQr.setImageBitmap(qrImage);
            }
        });

        clipManager = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addressPreBut:
                AddressShowActivity.this.finish();
                break;
            case R.id.copyAddressButton:
                ClipData mClipData = ClipData.newPlainText("Label", ethWallet.getAddress());
                clipManager.setPrimaryClip(mClipData);
                Toast.makeText(AddressShowActivity.this, "钱包收款地址复制成功", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
