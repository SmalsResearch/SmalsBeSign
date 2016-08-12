/*
 * eID Applet Project.
 * Copyright (C) 2008-2009 FedICT.
 * Copyright (C) 2014 e-Contract.be BVBA.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see 
 * http://www.gnu.org/licenses/.
 */

package be.smals.research.bulksign.desktopapp.eid.external.shared;

import be.smals.research.bulksign.desktopapp.eid.external.shared.annotation.HttpHeader;
import be.smals.research.bulksign.desktopapp.eid.external.shared.annotation.MessageDiscriminator;
import be.smals.research.bulksign.desktopapp.eid.external.shared.annotation.ProtocolStateAllowed;
import be.smals.research.bulksign.desktopapp.eid.external.shared.annotation.ResponsesAllowed;
import be.smals.research.bulksign.desktopapp.eid.external.shared.protocol.ProtocolState;

/**
 * Continue Insecure Message transfer object.
 * 
 * @author Frank Cornelis
 * 
 */
@ProtocolStateAllowed(ProtocolState.INSECURE)
@ResponsesAllowed({ IdentificationRequestMessage.class, AuthenticationRequestMessage.class, AdministrationMessage.class,
		SignRequestMessage.class, FilesDigestRequestMessage.class, FinishedMessage.class })
public class ContinueInsecureMessage extends AbstractProtocolMessage {
	@HttpHeader(TYPE_HTTP_HEADER)
	@MessageDiscriminator
	public static final String TYPE = ContinueInsecureMessage.class.getSimpleName();
}