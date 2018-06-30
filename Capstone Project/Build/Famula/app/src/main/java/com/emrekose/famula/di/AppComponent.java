package com.emrekose.famula.di;


import com.emrekose.famula.FamulaApp;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        ActivityBuilder.class})
public interface AppComponent extends AndroidInjector<FamulaApp>{
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<FamulaApp> {}
}
