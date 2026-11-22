package br.com.lavicameta.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.util.EnumMap;
import java.util.Map;

/** Gera o QR Code do PIX a partir do código copia e cola retornado pelo gateway. */
public final class QrCodeGenerator {

    private QrCodeGenerator() {
    }

    public static Bitmap gerar(String conteudo, int tamanhoPx) {
        try {
            Map<EncodeHintType, Object> dicas = new EnumMap<>(EncodeHintType.class);
            dicas.put(EncodeHintType.MARGIN, 1);
            dicas.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix matriz = new QRCodeWriter().encode(conteudo, BarcodeFormat.QR_CODE, tamanhoPx, tamanhoPx, dicas);
            int[] pixels = new int[tamanhoPx * tamanhoPx];
            for (int y = 0; y < tamanhoPx; y++) {
                for (int x = 0; x < tamanhoPx; x++) {
                    pixels[y * tamanhoPx + x] = matriz.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            return Bitmap.createBitmap(pixels, tamanhoPx, tamanhoPx, Bitmap.Config.ARGB_8888);
        } catch (WriterException ex) {
            return null;
        }
    }
}
