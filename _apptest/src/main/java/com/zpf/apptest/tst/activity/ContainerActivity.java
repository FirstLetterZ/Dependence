//package com.zpf.apptest.tst.activity;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.os.Bundle;
//import android.view.KeyEvent;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//import com.zpf.api.ICancelable;
//import com.zpf.frame.ICustomWindow;
//import com.zpf.api.IManager;
//import com.zpf.frame.OnActivityResultListener;
//import com.zpf.frame.OnAttachListener;
//import com.zpf.apptest.tst.IViewContainer;
//import com.zpf.tool.global.CentralManager;
//
//import java.lang.reflect.Type;
//
///**
// * 基于Activity的视图容器层
// * Created by ZPF on 2018/6/14.
// */
//public abstract class ContainerActivity extends Activity implements IViewContainer , OnActivityResultListener {
//    private boolean isLauncher;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        Intent intent = getIntent();
//        initTheme(intent.getIntExtra(AppConst.TARGET_VIEW_THEME, -1));
//        String stackItemName = intent.getStringExtra(AppStackUtil.STACK_ITEM_NAME);
//        if ((stackItemName == null || stackItemName.length() == 0) && mViewProcessor != null) {
//            Class<?> cls = (Class<?>) intent.getSerializableExtra(AppConst.TARGET_VIEW_CLASS);
//            if (cls != null) {
//                stackItemName = cls.getName();
//                intent.putExtra(AppStackUtil.STACK_ITEM_NAME, stackItemName);
//            }
//        }
//        super.onCreate(savedInstanceState);
//        //防止初次安装从后台返回的重启问题
//        isLauncher = (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction()));
//        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0 && isLauncher) {
//            finish();
//            return;
//        }
//        initWindow();
//        mViewProcessor = initViewProcessor();
//        if (mViewProcessor != null) {
//            mController.addListener(mViewProcessor, null);
//            mViewProcessor.initWindow(getWindow());
//            setContentView(mViewProcessor.getView());
//        } else {
//            LogUtil.w("IViewProcessor is null!");
//        }
//        initView(savedInstanceState);
//        mController.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onRestart() {
//        super.onRestart();
//        mController.onRestart();
//        onVisibleChanged(true);
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mController.onStart();
//        onVisibleChanged(true);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mController.onResume();
//        onActivityChanged(true);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mController.onPause();
//        onActivityChanged(false);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        mController.onStop();
//        onVisibleChanged(false);
//    }
//
//    @Override
//    public void onDestroy() {
//        mController.onDestroy();
//        super.onDestroy();
//        loadingManager = null;
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (!mController.onInterceptBackPress() && !close()) {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (mController.onKeyDown(keyCode, event)) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (mController.onKeyUp(keyCode, event)) {
//            return true;
//        }
//        return super.onKeyUp(keyCode, event);
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        if (outState != null) {
//            mController.onSaveInstanceState(outState);
//        }
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState != null) {
//            mController.onRestoreInstanceState(savedInstanceState);
//        }
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        Bundle oldParams = getIntent().getExtras();
//        Bundle newParams = intent.getExtras();
//        if (oldParams != null) {
//            if (newParams != null) {
//                oldParams.putAll(newParams);
//            }
//            mParams = oldParams;
//        } else {
//            mParams = newParams;
//        }
//        if (mParams != null) {
//            intent.putExtras(mParams);
//        }
//        super.onNewIntent(intent);
//        setIntent(intent);
//        onParamChanged(mParams);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        mController.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        mController.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    @LifecycleState
//    public int getState() {
//        return mController.getState();
//    }
//
//    @Override
//    public boolean living() {
//        return mController.living();
//    }
//
//    @Override
//    public boolean interactive() {
//        return mController.interactive();
//    }
//
//    @Override
//    public boolean visible() {
//        return mController.visible();
//    }
//
//    @Override
//    public Context getContext() {
//        return this;
//    }
//
//    @Override
//    public Intent getIntent() {
//        return super.getIntent();
//    }
//
//    @Override
//    public Activity getCurrentActivity() {
//        return this;
//    }
//
//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//    }
//
//    @Override
//    public void startActivity(Intent intent, @Nullable Bundle options) {
//        super.startActivity(intent, options);
//    }
//
//    @Override
//    public void startActivities(Intent[] intents) {
//        super.startActivities(intents);
//    }
//
//    @Override
//    public void startActivities(Intent[] intents, @Nullable Bundle options) {
//        super.startActivities(intents, options);
//    }
//
//    @Override
//    public void startActivityForResult(Intent intent, int requestCode) {
//        super.startActivityForResult(intent, requestCode);
//        StackAnimUtil.onPush(this, intent.getIntExtra(AppConst.ANIM_TYPE, 0));
//    }
//
//    @SuppressLint("RestrictedApi")
//    @Override
//    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
//        super.startActivityForResult(intent, requestCode, options);
//        StackAnimUtil.onPush(this, intent.getIntExtra(AppConst.ANIM_TYPE, 0));
//    }
//
//    @Override
//    public void show(final ICustomWindow window) {
//        CentralManager.runOnMainTread(new Runnable() {
//            @Override
//            public void run() {
//                mController.show(window);
//            }
//        });
//    }
//
//    @Override
//    public boolean close() {
//        return loadingManager != null && loadingManager.hideLoading() || mController.close();
//    }
//
//    @Override
//    public IManager<ICancelable> getCancelableManager() {
//        return mController.getCancelableManager();
//    }
//
//    @Override
//    public boolean addListener(Object listener, @Nullable Type listenerClass) {
//        return mController.addListener(listener, listenerClass);
//    }
//
//    @Override
//    public boolean removeListener(Object listener, @Nullable Type listenerClass) {
//        return mController.removeListener(listener, listenerClass);
//    }
//
//    @Override
//    public void finishWithResult(int resultCode, Intent data) {
//        setResult(resultCode, data);
//        this.finish();
//    }
//
//    @Override
//    public void finish() {
//        super.finish();
//        StackAnimUtil.onPoll(this, getIntent().getIntExtra(AppConst.ANIM_TYPE, 0));
//    }
//
//    @Override
//    public boolean hideLoading() {
//        return loadingManager != null && loadingManager.hideLoading();
//    }
//
//    @Override
//    public void setLoadingListener(OnAttachListener onAttachListener) {
//        if (loadingManager != null) {
//            loadingManager.setLoadingListener(onAttachListener);
//        }
//    }
//
//    @Override
//    public void showLoading() {
//        showLoading(getString(R.string.default_request_loading));
//    }
//
//    @Override
//    public void showLoading(Object message) {
//        if (living()) {
//            if (loadingManager == null) {
//                loadingManager = new LoadingManagerImpl(getContext());
//            }
//            loadingManager.showLoading(message);
//        }
//    }
//
//    @Override
//    public Object invoke(String name, Object params) {
//        return null;
//    }
//
//    @Override
//    public void setLoadingManager(ILoadingManager loadingManager) {
//        this.loadingManager = loadingManager;
//    }
//
//    @NonNull
//    @Override
//    public Bundle getParams() {
//        if (mParams == null) {
//            mParams = getIntent().getExtras();
//            if (mParams == null) {
//                mParams = new Bundle();
//            }
//        }
//        if (isLauncher) {
//            IContainerHelper containerHelper = CentralManager.getInstance(IContainerHelper.class);
//            Class<?> launcherClass = null;
//            if (containerHelper != null) {
//                launcherClass = containerHelper.getLaunchProcessorClass(null);
//            }
//            if (launcherClass == null) {
//                launcherClass = launcherViewProcessorClass();
//            }
//            if (launcherClass == null) {
//                launcherClass = defViewProcessorClass();
//            }
//            if (launcherClass == null && containerHelper != null) {
//                launcherClass = containerHelper.getErrorProcessorClass(null);
//            }
//            if (launcherClass != null) {
//                mParams.putSerializable(AppConst.TARGET_VIEW_CLASS, launcherClass);
//            }
//        }
//        return mParams;
//    }
//
//    @Override
//    public int getContainerType() {
//        return ContainerType.CONTAINER_ACTIVITY;
//    }
//
//    @Override
//    public boolean setProcessorLinker(IViewLinker linker) {
//        try {
//            mViewProcessor.onReceiveLinker(linker);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @Override
//    public IViewContainer getParentContainer() {
//        Activity parentActivity = getParent();
//        if (parentActivity instanceof IViewContainer) {
//            return ((IViewContainer) parentActivity);
//        }
//        return null;
//    }
//
//    @Override
//    @Nullable
//    public IViewProcessor getViewProcessor() {
//        return mViewProcessor;
//    }
//
//    @Override
//    public INavigator<Class<? extends IViewProcessor>> getNavigator() {
//        return null;
//    }
//
//    @Override
//    public void onParamChanged(Bundle newParams) {
//        mController.onParamChanged(newParams);
//    }
//
//    @Override
//    public void onVisibleChanged(boolean visible) {
//        mController.onVisibleChanged(visible);
//    }
//
//    @Override
//    public void onActivityChanged(boolean activity) {
//        mController.onActivityChanged(activity);
//    }
//
//    protected void initTheme(int themeId) {
//        if (themeId > 0) {
//            setTheme(themeId);
//        }
//    }
//
//    protected void initWindow() {
//        try {
//            setRequestedOrientation(getParams().getInt(AppConst.TARGET_VIEW_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
//        } catch (Exception e) {
//            //
//        }
//        if (getParams().getBoolean(AppConst.TARGET_STATUS_TRANSLUCENT, true)) {
//            StatusBarUtil.setStatusBarTranslucent(getWindow());
//        }
//    }
//
//    protected IViewProcessor initViewProcessor() {
//        return ContainerController.createViewProcessor(this, getParams(), defViewProcessorClass());
//    }
//
//    protected void initView(@Nullable Bundle savedInstanceState) {
//
//    }
//
//    protected Class<? extends IViewProcessor> defViewProcessorClass() {
//        return null;
//    }
//
//    protected Class<? extends IViewProcessor> launcherViewProcessorClass() {
//        return null;
//    }
//
//}