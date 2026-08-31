package edu.sdgku.stepcounter.shared.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import edu.sdgku.stepcounter.shared.model.FitnessData

class FirebaseRepository (
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance(),
    ){
    private val fitnessDocument = database
        .collection(FirestoreConstants.FITNESS_COLLECTION)
        .document(FirestoreConstants.DEMO_USER_DOCUMENT)

    fun listenToFitnessData(
        onDataChanged: (FitnessData) -> Unit,
        onError: (Exception) -> Unit = {}): ListenerRegistration {
            return fitnessDocument.addSnapshotListener { snapshot, exception ->
                    if (exception != null){
                        onError(exception)
                        return@addSnapshotListener
                        }
                    if (snapshot == null || !snapshot.exists()){
                         return@addSnapshotListener
                        }
                    val fitnessData = FitnessData(
                        dailyGoal = snapshot.getLong(FirestoreConstants.FIELD_DAILY_GOAL) ?: 10000,
                        steps = snapshot.getLong(FirestoreConstants.FIELD_STEPS) ?: 0,
                        heartRate = snapshot.getLong(FirestoreConstants.FIELD_HEART_RATE) ?: 72
                        )
                    onDataChanged(fitnessData)
                }
        }

    fun saveFitnessData(
        fitnessData: FitnessData,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {},
        ){
            val data = mapOf(
                FirestoreConstants.FIELD_DAILY_GOAL to fitnessData.dailyGoal,
                FirestoreConstants.FIELD_STEPS to fitnessData.steps,
                FirestoreConstants.FIELD_HEART_RATE to fitnessData.heartRate,
                FirestoreConstants.FIELD_UPDATED_AT to Timestamp.now()
            )
            fitnessDocument.set(data)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener {  exception -> onError(exception) }
    } // end of fun saveFitnessData

    fun updateDailyGoal(
        dailyGoal: Long,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {},
        ){
        updateFields(
            fields = mapOf(FirestoreConstants.FIELD_DAILY_GOAL to dailyGoal),
            onSuccess = onSuccess,
            onError = onError
        )
    } // end of fun updateDailyGoal

    private fun updateFields(
        fields: Map<String, Any>,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {},
        ){
        val fieldsWithTimestamp = fields + mapOf(
            FirestoreConstants.FIELD_UPDATED_AT to Timestamp.now()
        )
        fitnessDocument.set(fieldsWithTimestamp, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener {  exception -> onError(exception) }
    }
}