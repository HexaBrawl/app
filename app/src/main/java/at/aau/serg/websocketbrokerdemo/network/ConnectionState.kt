package at.aau.serg.websocketbrokerdemo.network

/**
 * Status der STOMP/WebSocket-Verbindung.
 *
 * Vom [Stomp] als StateFlow exponiert; UI-Schichten (z. B. das
 * Reconnecting-Overlay im Game-Screen) reagieren darauf.
 *
 *  - [Connected]       Normalbetrieb, WebSocket steht.
 *  - [Reconnecting]    WebSocket ist weggebrochen; Stomp probiert
 *                      automatisch erneut. UI zeigt Wartebildschirm.
 *  - [LostPermanently] Nach maximal vorgesehenen Versuchen kein
 *                      erfolgreicher Wiederaufbau. UI bietet dem
 *                      User die Rueckkehr zum Menue an.
 */
enum class ConnectionState {
    Connected,
    Reconnecting,
    LostPermanently
}
