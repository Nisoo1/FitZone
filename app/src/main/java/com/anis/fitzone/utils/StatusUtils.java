package com.anis.fitzone.utils;

import android.content.Context;

import com.anis.fitzone.R;
import com.anis.fitzone.modeles.Seance;

public final class StatusUtils {

    private StatusUtils() {
    }

    public static int colorFor(Context context, String displayStatus) {
        int colorRes;
        if (Seance.STATUT_VALIDEE.equals(displayStatus)) {
            colorRes = R.color.status_validated;
        } else if (Seance.STATUT_SOUMISE.equals(displayStatus)) {
            colorRes = R.color.status_submitted;
        } else if (Seance.STATUT_EN_RETARD.equals(displayStatus)) {
            colorRes = R.color.status_late;
        } else {
            colorRes = R.color.status_todo;
        }
        return context.getColor(colorRes);
    }

    public static String labelFor(Context context, String displayStatus) {
        if (Seance.STATUT_VALIDEE.equals(displayStatus)) {
            return context.getString(R.string.status_validated);
        } else if (Seance.STATUT_SOUMISE.equals(displayStatus)) {
            return context.getString(R.string.status_submitted);
        } else if (Seance.STATUT_EN_RETARD.equals(displayStatus)) {
            return context.getString(R.string.status_late);
        }
        return context.getString(R.string.status_todo);
    }
}
