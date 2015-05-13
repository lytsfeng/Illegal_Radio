package com.ldkj.illegal_radio.activitys.base;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import com.ldkj.illegal_radio.views.CustomToast;

import java.util.ArrayDeque;
import java.util.Queue;


/**
 * Created by john on 15-4-10.
 */
public class ActivityBase extends Activity {

    private ProgressDialog progressDialog;
    Queue<Float[]> spceQueue = new ArrayDeque<Float[]>();

    /**
     * 显示toast信息
     * @param pMsgId　要显示的信息的ID
     */
    protected void showMsg(int pMsgId){
        CustomToast.showToast(this, pMsgId, Toast.LENGTH_SHORT);
    }
    /**
     * 显示toast信息
     * @param pMsg 要显示的信息
     */
    protected void showMsg(String pMsg) {
        CustomToast.showToast(this,pMsg,Toast.LENGTH_SHORT);
    }
    /**
     * 通过action打开activity
     * @param pAction
     */
    protected void openActivity(String pAction){
        startActivity(new Intent(pAction));
    }

    /**
     * 打开activity
     * @param cls
     */
    protected void openActivity(Class<?> cls){
        Intent _Intent = new Intent(this,cls);
        startActivity(_Intent);
    }

    /**
     * 显示等待对话框
     * @param pTitleResID
     * @param pMessageResID
     */
    protected void showProgressDialog(int pTitleResID, int pMessageResID) {
        showProgressDialog(getString(pTitleResID), getString(pMessageResID));
    }

    /**
     * 显示等待对话框
     * @param pTitle
     * @param pMessage
     */
    protected void showProgressDialog(String pTitle,String pMessage){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(pTitle);
        progressDialog.setMessage(pMessage);
        progressDialog.show();
    }


    /**
     * 取消等待对话框
     */
    protected void ｄismissProgressDialog() {
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }


    protected void showFreagment(int fragmentID, Fragment fragment){
        FragmentManager _Manager = getFragmentManager();
        FragmentTransaction _Transaction = _Manager.beginTransaction();
        _Transaction.replace(fragmentID,fragment);
        _Transaction.commit();
    }

    protected void removeFreagment(int fragmentID){
        FragmentManager _Manager = getFragmentManager();
        FragmentTransaction _Transaction = _Manager.beginTransaction();
        _Transaction.remove(_Manager.findFragmentById(fragmentID));
        _Transaction.commit();
    }

    protected  Fragment getFreagment(int fragmentid){
        return getFragmentManager().findFragmentById(fragmentid);
    }

    protected void startService(Class<?> cls) {
        startService(new Intent(this, cls));
    }
    protected void stopService(Class<?> cls) {
        stopService(new Intent(this, cls));
    }

}
