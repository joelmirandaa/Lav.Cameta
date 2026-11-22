package br.com.lavicameta.util;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import br.com.lavicameta.R;

/** Notificação local de fim de ciclo (UC08 passo 4) enquanto o app acompanha o pedido. */
public final class NotificationHelper {

    private static final String CANAL = "ciclos";

    private NotificationHelper() {
    }

    public static void criarCanal(Context context) {
        NotificationChannel canal = new NotificationChannel(CANAL, context.getString(R.string.notificacao_canal),
                NotificationManager.IMPORTANCE_DEFAULT);
        canal.setDescription(context.getString(R.string.notificacao_canal_descricao));
        context.getSystemService(NotificationManager.class).createNotificationChannel(canal);
    }

    public static void notificar(Context context, int id, String titulo, String texto, Intent abrir) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PendingIntent pendente = PendingIntent.getActivity(context, id, abrir,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL)
                .setSmallIcon(R.drawable.ic_circle_check)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setColor(ContextCompat.getColor(context, R.color.agua))
                .setAutoCancel(true)
                .setContentIntent(pendente);
        NotificationManagerCompat.from(context).notify(id, builder.build());
    }
}
