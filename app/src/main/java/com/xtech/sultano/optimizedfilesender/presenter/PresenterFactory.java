package com.xtech.sultano.optimizedfilesender.presenter;

import android.content.Context;

import com.xtech.sultano.optimizedfilesender.model.Model.Model;
import com.xtech.sultano.optimizedfilesender.view.UiView;

public class PresenterFactory<T extends Presenter> {
    private Model mModel;
    private Presenter mPresenter;

    // Singleton design pattern
    public Presenter create(Presenter presenter, UiView uiView, Context context) {
        if(presenter == null){
            mModel = new Model();
            return new Presenter(uiView, mModel, context);
        }
        else{
            mPresenter = presenter;
            return presenter;
        }
    }
}