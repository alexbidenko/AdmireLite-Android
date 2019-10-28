package social.admire.admire.main.mvp

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

@InjectViewState
class MainActivityPresenter : MvpPresenter<MainActivityView>() {

    fun initFragments() {
        viewState.initFragments()
    }
}