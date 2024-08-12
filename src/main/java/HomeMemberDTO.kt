package com.example.demo

import java.io.Serializable
import javax.persistence.Id


/**
 * @author abstergo
 */
data class HomeMemberDTO(
    @Id
    var id: Long? = null,
    var homeId: Long? = null,
    var userId: Long? = null,
    var type: Byte? = null,
    var userName: String? = null,
    var enabled: Byte? = null
) : Serializable {


    companion object {
        private val serialVersionUID = -986028794879391364L
    }
}
