package com.mohali.store.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohali.store.data.models.User
import com.mohali.store.data.models.UserRole
import com.mohali.store.data.remote.FirebaseRepository
import com.mohali.store.utils.PrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val prefsManager: PrefsManager
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun login(
        username: String,
        password: String,
        onResult: (success: Boolean, isAdmin: Boolean, error: String?) -> Unit
    ) {
        viewModelScope.launch {
            // Check local admin first
            val result = firebaseRepository.loginAdmin(username, password)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                _currentUser.value = user
                prefsManager.saveUser(user)
                onResult(true, user.role == UserRole.ADMIN, null)
                return@launch
            }
            // Try Firebase login
            val email = "$username@mohali.store"
            val fbResult = firebaseRepository.loginWithEmailPassword(email, password)
            if (fbResult.isSuccess) {
                val user = fbResult.getOrNull()!!
                _currentUser.value = user
                prefsManager.saveUser(user)
                onResult(true, user.role == UserRole.ADMIN, null)
            } else {
                onResult(false, false, "اسم المستخدم أو كلمة المرور غير صحيحة")
            }
        }
    }

    fun changePassword(
        newPassword: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            val result = firebaseRepository.changePassword(newPassword)
            if (result.isSuccess) {
                // Also update local prefs
                prefsManager.savePassword(newPassword)
                onResult(true, null)
            } else {
                onResult(false, result.exceptionOrNull()?.message)
            }
        }
    }

    fun isLoggedIn(): Boolean = prefsManager.isLoggedIn()
    fun getSavedUser(): User? = prefsManager.getUser()
}
