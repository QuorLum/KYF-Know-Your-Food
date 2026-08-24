package com.kyf.knowyourfood.data.repository

import com.kyf.knowyourfood.data.local.dao.ProfileDao
import com.kyf.knowyourfood.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val profileDao: ProfileDao) {

    fun getAllProfiles(): Flow<List<ProfileEntity>> = profileDao.getAllProfiles()

    suspend fun getProfileById(id: Long): ProfileEntity? = profileDao.getProfileById(id)

    suspend fun insertProfile(profile: ProfileEntity): Long = profileDao.insertProfile(profile)

    suspend fun updateProfile(profile: ProfileEntity) = profileDao.updateProfile(profile)

    suspend fun deleteProfile(profile: ProfileEntity) = profileDao.deleteProfile(profile)
}
