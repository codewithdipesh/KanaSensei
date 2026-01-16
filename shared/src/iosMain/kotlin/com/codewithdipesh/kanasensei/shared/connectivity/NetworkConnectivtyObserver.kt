package com.codewithdipesh.kanasensei.shared.connectivity


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class NetworkConnectivityObserver: ConnectivityObserver {

    override fun observe(): Flow<ConnectivityObserver.Status> {
        // TODO: Implement iOS connectivity monitoring using Network framework
        // For now, assume always available
        return flowOf(ConnectivityObserver.Status.Available)
    }
}