package top.niunaijun.blackboxa.view.list

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ferfalk.simplesearchview.SimpleSearchView
import top.niunaijun.blackboxa.R
import top.niunaijun.blackboxa.bean.AppInfo
import top.niunaijun.blackboxa.databinding.ActivityListBinding
import top.niunaijun.blackboxa.util.InjectionUtil
import top.niunaijun.blackboxa.util.inflate


class ListActivity : AppCompatActivity() {

    private val viewBinding: ActivityListBinding by inflate()

    private lateinit var mAdapter: ListAdapter

    private lateinit var viewModel: ListViewModel

    private var appList: List<AppInfo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.toolbarLayout.toolbar)
        viewBinding.toolbarLayout.toolbar.setTitle(R.string.installed_app)
        viewBinding.toolbarLayout.toolbar.setNavigationOnClickListener {
            finish()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        mAdapter = ListAdapter()
        viewBinding.recyclerView.adapter = mAdapter
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)

        mAdapter.setOnItemClick { _, _, data ->
            finishWithResult(data.packageName)
        }

        initSearchView()
        initViewModel()
    }

    private fun initSearchView() {
        viewBinding.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
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
        val onlyShowXp = intent.getBooleanExtra("onlyShowXp", false)
        viewBinding.stateView.showLoading()

        if (onlyShowXp) {
            viewModel.getInstalledModules()
            viewBinding.toolbarLayout.toolbar.setTitle(R.string.installed_module)
        } else {
            viewModel.getInstallAppList()
            viewBinding.toolbarLayout.toolbar.setTitle(R.string.installed_app)
        }

        viewModel.appsLiveData.observe(this) {
            if (it != null) {
                this.appList = it
                viewBinding.searchView.setQuery("", false)
                filterApp("")
                if (it.isNotEmpty()) {
                    viewBinding.stateView.showContent()
                } else {
                    viewBinding.stateView.showEmpty()
                }
            }
        }
    }

    private fun filterApp(newText: String) {
        val newList = this.appList.filter {
            it.name.contains(newText, true) or it.packageName.contains(newText, true)
        }
        mAdapter.replaceData(newList)
    }

    private val openDocumentedResult = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.run {
            finishWithResult(it.toString())
        }
    }

    private fun finishWithResult(source: String) {
        intent.putExtra("source", source)
        setResult(Activity.RESULT_OK, intent)
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        window.peekDecorView()?.run {
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
        finish()
    }


    override fun onBackPressed() {
        if (viewBinding.searchView.isSearchOpen) {
            viewBinding.searchView.closeSearch()
        } else {
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

}