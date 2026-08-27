package edu.sdgku.stepcounter.shared.presentation

import edu.sdgku.stepcounter.shared.model.FitnessData

data class FitnessUiState (
    val fitnessData: FitnessData = FitnessData(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    ){
}