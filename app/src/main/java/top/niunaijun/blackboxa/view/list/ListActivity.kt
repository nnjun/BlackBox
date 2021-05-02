package top.niunaijun.blackboxa.view.list

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferfalk.simplesearchview.SimpleSearchView
import com.roger.catloadinglibrary.CatLoadingView
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.databinding.ActivityListBinding
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.util.LoadingUtil
import top.niunaijun.blackboxa.util.inflate
import top.niunaijun.blackboxa.util.toast


class ListActivity : AppCompatActivity() {

    private val viewBinding: ActivityListBinding by inflate()

    private lateinit var mAdapter: ListAdapter

    private lateinit var viewModel: ListViewModel

    private lateinit var loadingView: CatLoadingView

    private var appList :List<AppInfo> = ArrayList<AppInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbarLayout.toolbar)
        mAdapter = ListAdapter()
        viewBinding.recyclerView.adapter = mAdapter
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)

        mAdapter.setOnItemClick { _, _, data ->
            finishWithPath(data.sourceDir)
        }

        initSearchView()
        initViewModel()
    }

    private fun initSearchView() {
        viewBinding.searchView.setOnQueryTextListener(object :SimpleSearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String): Boolean {
                filterApp(newText)
                return true
            }

            override fun onQueryTextCleared(): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, InjectionUtil.getListFactory()).get(ListViewModel::class.java)
        viewBinding.stateView.showLoading()
        viewModel.getInstalledApps()
        viewModel.appsLiveData.observe(this) {
            if (it != null) {
                this.appList = it
                filterApp("")
                if (it.isNotEmpty()) {
                    viewBinding.stateView.showContent()
                } else {
                    viewBinding.stateView.showEmpty()
                }
            }
        }

        viewModel.copyFileLiveData.observe(this) {
            hideLoading()
            if (TextUtils.isEmpty(it)) {
                finishWithPath(it)
            }else{
                toast("文件读取失败")
            }
        }
    }

    private fun filterApp(newText: String) {
        val newList = this.appList.filter {
            it.name.contains(newText,true) or it.packageName.contains(newText,true)
        }
        mAdapter.replaceData(newList)
    }

    private val openDocumentedResult = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.run {
            copyFile(this)
        }
    }


    private fun finishWithPath(apkPath: String) {
        intent.putExtra("apkPath", apkPath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun copyFile(uri: Uri) {
        showLoading()
        viewModel.copyFile(uri)
    }

    override fun onBackPressed() {
        if(viewBinding.searchView.isSearchOpen){
            viewBinding.searchView.closeSearch()
        }else{
            super.onBackPressed()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.list_choose) {
            openDocumentedResult.launch("application/vnd.android.package-archive")
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
        val item = menu!!.findItem(R.id.list_search)
        viewBinding.searchView.setMenuItem(item)

        return true
    }

    private fun showLoading() {
        if (!this::loadingView.isInitialized) {
            loadingView = CatLoadingView()
        }
        LoadingUtil.showLoading(loadingView, supportFragmentManager)
    }


    private fun hideLoading() {
        if (loadingView.isAdded && loadingView.isResumed) {
            loadingView.dismiss()
        }
    }
}