/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.mindorks.framework.mvvm.ui.main;

import android.text.TextUtils;
import android.util.Log;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mindorks.framework.mvvm.data.DataManager;
import com.mindorks.framework.mvvm.data.model.db.Question;
import com.mindorks.framework.mvvm.data.model.others.QuestionCardData;
import com.mindorks.framework.mvvm.ui.base.BaseViewModel;
import com.mindorks.framework.mvvm.utils.rx.SchedulerProvider;

import java.util.List;

/**
 * Created by amitshekhar on 07/07/17.
 */

public class MainViewModel extends BaseViewModel<MainNavigator> {

    private static final String TAG = "MainViewModel";

    public static final int NO_ACTION = -1, ACTION_ADD_ALL = 0, ACTION_DELETE_SINGLE = 1;

    private final ObservableField<String> appVersion = new ObservableField<>();

    private final MutableLiveData<List<QuestionCardData>> questionCardData;

    private final ObservableList<QuestionCardData> questionDataList = new ObservableArrayList<>();

    private final ObservableField<String> userEmail = new ObservableField<>();

    private final ObservableField<String> questionsAnswered = new ObservableField<>();

    private final ObservableField<String> userName = new ObservableField<>();

    private final ObservableField<String> userProfilePicUrl = new ObservableField<>();


    private int action = NO_ACTION;

    public MainViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
        questionCardData = new MutableLiveData<>();
        loadQuestionCards();
    }

    public int getAction() {
        return action;
    }

    public ObservableField<String> getAppVersion() {
        return appVersion;
    }

    public ObservableField<String> getQuestionsAnswered() {
        return questionsAnswered;
    }

    public LiveData<List<QuestionCardData>> getQuestionCardData() {
        return questionCardData;
    }

    public ObservableList<QuestionCardData> getQuestionDataList() {
        return questionDataList;
    }

    public void setQuestionDataList(List<QuestionCardData> questionCardDatas) {
        action = ACTION_ADD_ALL;
        questionDataList.clear();
        questionDataList.addAll(questionCardDatas);
    }

    public ObservableField<String> getUserEmail() {
        return userEmail;
    }

    public ObservableField<String> getUserName() {
        return userName;
    }

    public ObservableField<String> getUserProfilePicUrl() {
        return userProfilePicUrl;
    }


    public void loadQuestionCards() {
        questionsAnswered.set("Questions Answered:0/6");
        getCompositeDisposable().add(getDataManager().resetAllQuestions()
                .flatMap(aBoolean -> getDataManager().getQuestionCardData())
                .doOnNext(list -> Log.d(TAG, "loadQuestionCards: " + list.size()))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(questionList -> {
                    if (questionList != null) {
                        Log.d(TAG, "loadQuestionCards: " + questionList.size());
                        action = ACTION_ADD_ALL;
                        questionCardData.setValue(questionList);
                    }
                }, throwable -> {
                    Log.d(TAG, "loadQuestionCards: " + throwable);
                }));
    }


    public void saveQuestionAsAnswered(Question question) {
        getCompositeDisposable().add(getDataManager().saveAnsweredQuestion(question).
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).
                subscribe(aBoolean -> {
                    updateQuestionsAnsweredCount();
                }, throwable -> {
                    getNavigator().handleError(throwable);
                }));
    }


    private void updateQuestionsAnsweredCount() {
        getCompositeDisposable().add(getDataManager().
                getAnsweredQuestionsCount().
                subscribeOn(getSchedulerProvider().io()).
                observeOn(getSchedulerProvider().ui()).
                subscribe((integer, throwable) -> {
                    if (throwable == null) {
                        questionsAnswered.set("Questions Answered:" + integer + "/6");
                    } else {
                        getNavigator().handleError(throwable);
                    }
                }));
    }


    public void logout() {
        setIsLoading(true);
        getCompositeDisposable().add(getDataManager().doLogoutApiCall()
                .doOnSuccess(response -> {
                    getDataManager().setUserAsLoggedOut();
                })
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    setIsLoading(false);
                    getNavigator().openLoginActivity();
                }, throwable -> {
                    setIsLoading(false);
                    getNavigator().handleError(throwable);
                }));
    }

    public void onNavMenuCreated() {
        final String currentUserName = getDataManager().getCurrentUserName();
        if (!TextUtils.isEmpty(currentUserName)) {
            userName.set(currentUserName);
        }

        final String currentUserEmail = getDataManager().getCurrentUserEmail();
        if (!TextUtils.isEmpty(currentUserEmail)) {
            userEmail.set(currentUserEmail);
        }

        final String profilePicUrl = getDataManager().getCurrentUserProfilePicUrl();
        if (!TextUtils.isEmpty(profilePicUrl)) {
            userProfilePicUrl.set(profilePicUrl);
        }
    }

    public void removeQuestionCard() {
        action = ACTION_DELETE_SINGLE;
        questionCardData.getValue().remove(0);
    }

    public void updateAppVersion(String version) {
        appVersion.set(version);
    }
}
