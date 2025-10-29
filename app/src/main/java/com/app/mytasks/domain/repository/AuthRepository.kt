package com.app.mytasks.domain.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * LoginRepository
 *
 * @author stephingeorge
 * @date 29/10/2025
 */
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun currentUser() = firebaseAuth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser?> =
        suspendCancellableCoroutine { cont ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    cont.resume(Result.success(result.user))
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(e))
                }
        }

    suspend fun register(email: String, password: String): Result<FirebaseUser?> =
        suspendCancellableCoroutine { cont ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    cont.resume(Result.success(result.user))
                }
                .addOnFailureListener { e ->
                    cont.resume(Result.failure(e))
                }
        }

    fun logout() {
        firebaseAuth.signOut()
    }
}
