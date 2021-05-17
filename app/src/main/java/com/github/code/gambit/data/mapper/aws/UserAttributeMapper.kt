package com.github.code.gambit.data.mapper.aws

import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.github.code.gambit.data.EntityMapper
import com.github.code.gambit.data.model.User
import javax.inject.Inject
import kotlin.Deprecated as KotlinDeprecated

class UserAttributeMapper
@Inject
constructor() : EntityMapper<List<AuthUserAttribute>, User> {

    override fun mapFromEntity(entity: List<AuthUserAttribute>): User {
        val user = User("", "", "", "", 0)
        entity.forEach {
            when (it.key) {
                AuthUserAttributeKey.name() -> {
                    user.name = it.value
                }
                AuthUserAttributeKey.email() -> {
                    user.email = it.value
                }
            }
        }
        return user
    }

    override fun mapToEntity(domainModel: User): List<AuthUserAttribute> {
        return listOf(
            AuthUserAttribute(AuthUserAttributeKey.email(), domainModel.email),
            AuthUserAttribute(AuthUserAttributeKey.name(), domainModel.name)
        )
    }

    @KotlinDeprecated("not to be used in this implementation")
    override fun mapFromEntityList(entities: List<List<AuthUserAttribute>>): List<User> {
        return listOf()
    }
}
