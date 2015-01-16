package com.cvte.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * @author Arthur
 */
public final class AppUtil {
    private static final String MENGYOU_APPKEY = "MENGYOU_APPKEY";

    public static String getAppKey(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(context.getPackageName(), 128);
            if (ai != null && ai.metaData != null) {
                String appKey = ai.metaData.getString(MENGYOU_APPKEY);
                if (!TextUtils.isEmpty(appKey)) {
                    return appKey.trim();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LogUtil.e("App Key Not Exist !");
        return null;
    }

    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static File getApkFile(Context context) {
        File file = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            file = new File(pi.applicationInfo.publicSourceDir);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String getCertificateSHA1Fingerprint(Context context) {
        PackageManager pm = context.getPackageManager();
        int flags = PackageManager.GET_SIGNATURES;
        StringBuffer hexString = new StringBuffer();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), flags);
            android.content.pm.Signature[] signatures = pi.signatures;
            byte[] cert = signatures[0].toByteArray();
            InputStream is = new ByteArrayInputStream(cert);
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate c = (X509Certificate) cf.generateCertificate(is);
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getPublicKey().getEncoded());
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toString(0xFF & publicKey[i]);
                if (appendString.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(appendString);
            }
            LogUtil.d("Certificate : " + hexString.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hexString.toString();
    }

    public static Intent createInstallIntent(File file) {
        //FIXME 14-11-10 seems not belong to this method
        // Add permission
        String[] arg1 = {"chmod", "705", file.getParent()};
        LinuxUtil.exec(arg1);
        String[] arg2 = {"chmod", "604", file.getPath()};
        LinuxUtil.exec(arg2);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return  intent;
    }

    public static String getAppName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }
}
