package hu.matemagyar.wge.repository

import hu.matemagyar.wge.entity.UserEntity
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface UserRepository : CrudRepository<UserEntity, Long>