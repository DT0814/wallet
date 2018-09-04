package lr.com.wallet.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by DT0814 on 2018/9/4.
 */

public class AlignmentTextView extends android.support.v7.widget.AppCompatTextView {
    private int mLineY = 0;//总行高
    private int mViewWidth;//TextView的总宽度
    private TextPaint paint;

    public AlignmentTextView(Context context) {
        super(context);
        init();
    }

    public AlignmentTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlignmentTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mViewWidth = getMeasuredWidth() - 5;//获取textview的实际宽度
        mLineY += getTextSize();
        String text = getText().toString();
        Layout layout = getLayout();
        int lineCount = layout.getLineCount();
        for (int i = 0; i < lineCount; i++) {//每行循环
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String lineText = text.substring(lineStart, lineEnd);//获取TextView每行中的内容
            if (needScale(lineText)) {
                if (i == lineCount - 1) {//最后一行不需要重绘
                    canvas.drawText(lineText, 0, mLineY, paint);
                } else {
                    float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, paint);
                    drawScaleText(canvas, lineText, width);
                }
            } else {
                canvas.drawText(lineText, 0, mLineY, paint);
            }
            mLineY += getLineHeight();//写完一行以后,高度增加一行的高度
        }
    }

    private void drawScaleText(Canvas canvas, String lineText, float lineWidth) {
        float x = 0;
        if (isFirstLineOfParagraph(lineText)) {
            String blanks = "";
            canvas.drawText(blanks, x, mLineY, paint);
            float width = StaticLayout.getDesiredWidth(blanks, paint);
            x += width;
            lineText = lineText.substring(3);
        }
        float interval = (mViewWidth - lineWidth) / (lineText.length() - 1);
        for (int i = 0; i < lineText.length(); ) {
            String character;
            if (!isEmojiCharacter(lineText.charAt(i))) {
                character = String.valueOf(lineText.substring(i, i += 2));
            } else {
                character = String.valueOf(lineText.charAt(i++));
            }
            float cw = StaticLayout.getDesiredWidth(character, paint);
            canvas.drawText(character, x, mLineY, paint);
            x += (cw + interval);
        }
    }

    private boolean isFirstLineOfParagraph(String lineText) {
        return lineText.length() > 3 &&
                lineText.charAt(0) == ' ' &&
                lineText.charAt(1) == ' ';
    }

    /**
     * 判断需不需要缩放.
     *
     * @param lineText 该行所有的文字
     * @return true 该行最后一个字符不是换行符false 该行最后一个字符是换行符
     */
    private boolean needScale(String lineText) {
        if (lineText.length() == 0) {
            return false;
        } else {
            return lineText.charAt(lineText.length() - 1) != '\n';
        }
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) &&
                (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) &&
                (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }
}
