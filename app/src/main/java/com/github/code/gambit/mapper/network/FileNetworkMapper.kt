package com.github.code.gambit.mapper.network

import com.github.code.gambit.EntityMapper
import com.github.code.gambit.entity.network.FileNetworkEntity
import com.github.code.gambit.model.File
import com.github.code.gambit.utility.fromBase64
import com.github.code.gambit.utility.toBase64
import javax.inject.Inject

class FileNetworkMapper
@Inject
constructor() : EntityMapper<FileNetworkEntity, File> {

    override fun mapFromEntity(entity: FileNetworkEntity): File {
        return File(
            id = "${entity.pk}__${entity.sk}".toBase64(),
            name = entity.ls1_sk,
            type = entity.type,
            timestamp = entity.sk.split("#")[1],
            size = "${entity.size} Mb",
            extension = entity.ls1_sk.split(".")[1]
        )
    }

    override fun mapToEntity(domainModel: File): FileNetworkEntity {
        return FileNetworkEntity(
            pk = domainModel.id.fromBase64().split("__")[0],
            sk = "FILE#${domainModel.timestamp}",
            ls1_sk = domainModel.name,
            size = domainModel.size.split(" ")[0].toInt(),
            type = domainModel.type
        )
    }

    override fun mapFromEntityList(entities: List<FileNetworkEntity>): List<File> {
        return entities.map { mapFromEntity(it) }
    }
}
