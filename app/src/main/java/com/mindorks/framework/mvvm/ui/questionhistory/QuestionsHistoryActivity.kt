package com.mindorks.framework.mvvm.ui.questionhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindorks.framework.mvvm.BR
import com.mindorks.framework.mvvm.R
import com.mindorks.framework.mvvm.ViewModelProviderFactory
import com.mindorks.framework.mvvm.data.DataManager
import com.mindorks.framework.mvvm.databinding.FragmentQuestionsHistoryBinding
import com.mindorks.framework.mvvm.ui.base.BaseActivity
import com.mindorks.framework.mvvm.utils.rx.SchedulerProvider
import javax.inject.Inject

class QuestionsHistoryActivity : BaseActivity<FragmentQuestionsHistoryBinding, QuestionHistoryViewModel>(), QuestionHistoryNavigator {


    @Inject
    lateinit var factory: ViewModelProviderFactory

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    private lateinit var questionHistoryViewModel: QuestionHistoryViewModel

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }


    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, QuestionsHistoryActivity::class.java)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_questions_history
    }

    override fun getViewModel(): QuestionHistoryViewModel {
        questionHistoryViewModel = ViewModelProviders.of(this, factory).get(QuestionHistoryViewModel::class.java)
        return questionHistoryViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivity()
        listenToViewModeLiveData()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId==android.R.id.home){
            val upIntent = NavUtils.getParentActivityIntent(this)
            upIntent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (NavUtils.shouldUpRecreateTask(this, upIntent!!)) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this) // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent!!) // Navigate up to the closest parent
                        .startActivities()
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(this, upIntent!!)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActivity(){
        viewModel.loadQuestionCards()
        setSupportActionBar(viewDataBinding.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    private fun listenToViewModeLiveData() {
        viewModel.questionCardData.observe(this, {
            if (it.isNullOrEmpty()) {
                viewDataBinding.recHistory.visibility = View.GONE
                viewDataBinding.tvNoData.visibility = View.VISIBLE
            } else {
                viewDataBinding.tvNoData.visibility = View.GONE
                viewDataBinding.recHistory.apply {
                    adapter = QuestionHistoryAdapter(it)
                    layoutManager = LinearLayoutManager(this@QuestionsHistoryActivity)
                }
            }

        })
    }

    override fun handleError(throwable: Throwable) {
        throwable.printStackTrace()
    }


}
