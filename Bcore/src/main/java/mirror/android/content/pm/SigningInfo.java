package mirror.android.content.pm;

import android.content.pm.PackageParser;

import mirror.RefClass;
import mirror.RefObject;

/**
 * Created by Milk on 4/16/21.
 * * ∧＿∧
 * (`･ω･∥
 * 丶　つ０
 * しーＪ
 * 此处无Bug
 */
public class SigningInfo {
    public static Class<?> TYPE = RefClass.load(UserInfo.class, "android.content.pm.SigningInfo");
    public static RefObject<PackageParser.SigningDetails> mSigningDetails;
}
