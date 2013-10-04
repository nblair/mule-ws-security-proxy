/**
 * 
 */
package com.github.nblair.wssecurity;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * No-op implementation of {@link CallbackHandler}.
 * A {@link CallbackHandler} is required when configuring WSS4J. Typically it is used to call
 * {@link org.apache.ws.security.WSPasswordCallback#setPassword(String)}; in this example we accomplish the same by 
 * configuring the appropriate properties for {@link org.apache.ws.security.components.crypto.Merlin}.
 * 
 * @author nblair
 */
public final class NoopCallbackHandler implements CallbackHandler {

	/* (non-Javadoc)
	 * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
	 */
	@Override
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
	}

}
