package com.mindorks.framework.mvvm.ui.questionhistory

import androidx.lifecycle.MutableLiveData
import com.mindorks.framework.mvvm.data.DataManager
import com.mindorks.framework.mvvm.data.model.others.QuestionCardData
import com.mindorks.framework.mvvm.ui.base.BaseViewModel
import com.mindorks.framework.mvvm.utils.rx.SchedulerProvider

class QuestionHistoryViewModel(dataManager: DataManager, schedulerProvider: SchedulerProvider) :
        BaseViewModel<QuestionHistoryNavigator>(dataManager, schedulerProvider) {

    val questionCardData: MutableLiveData<List<QuestionCardData>> = MutableLiveData()


    fun loadQuestionCards() {
        compositeDisposable.add(dataManager
                .answeredQuestionCardData
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({ questionList: List<QuestionCardData> ->
                    questionCardData.value = questionList
                }) { throwable: Throwable -> navigator.handleError(throwable) })
    }
}
