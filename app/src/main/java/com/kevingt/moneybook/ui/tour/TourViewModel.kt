package com.kevingt.moneybook.ui.tour

import androidx.lifecycle.ViewModel
import com.kevingt.moneybook.data.local.PreferenceHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TourViewModel @Inject constructor(
    preferenceHolder: PreferenceHolder
) : ViewModel() {

    var inviteMember by preferenceHolder.bindBoolean(true)

    var recordsSorting by preferenceHolder.bindBoolean(true)

    var rearrangeCategories by preferenceHolder.bindBoolean(true)

}