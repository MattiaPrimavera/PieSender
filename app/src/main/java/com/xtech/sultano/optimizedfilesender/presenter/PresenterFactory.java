package com.xtech.sultano.optimizedfilesender.presenter;

import com.xtech.sultano.optimizedfilesender.model.Model.Model;
import com.xtech.sultano.optimizedfilesender.view.UiView;

public class PresenterFactory<T extends Presenter> {
    private Presenter presenter;
    private Model mModel;

    // Singleton design pattern
    public Presenter create(UiView uiView) {
        if(presenter == null){
            mModel = new Model();
            return new Presenter(uiView, mModel);
        }
        else return presenter;
    }
}