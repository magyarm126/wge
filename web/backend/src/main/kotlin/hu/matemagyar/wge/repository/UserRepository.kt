package hu.matemagyar.wge.repository

import hu.matemagyar.wge.model.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface UserRepository : CrudRepository<User, Long> {

}