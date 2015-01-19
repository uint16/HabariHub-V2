package damagination.com.habarihub.radio;

/**
 * Created by Damas on 1/16/15.
 */
public interface IMediaPlayerThreadClient {

    /**
     * A callback made by a MediaPlayerThread onto its clients to indicate that a player is initializing.
     * @param message A message to propagate to the client
     */
    public void onInitializePlayerStart();

    /**
     * A callback made by a MediaPlayerThread onto its clients to indicate that a player was successfully initialized.
     */
    public void onInitializePlayerSuccess();

    /**
     *  A callback made by a MediaPlayerThread onto its clients to indicate that a player encountered an error.
     */
    public void onError();
}
