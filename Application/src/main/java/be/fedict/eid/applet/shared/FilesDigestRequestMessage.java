/*
 * eID Applet Project.
 * Copyright (C) 2008-2009 FedICT.
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

package be.fedict.eid.applet.shared;

import be.smals.research.bulksign.desktopapp.eid.external.shared.annotation.HttpHeader;
import be.smals.research.bulksign.desktopapp.eid.external.shared.annotation.MessageDiscriminator;
import be.smals.research.bulksign.desktopapp.eid.external.shared.annotation.NotNull;
import be.smals.research.bulksign.desktopapp.eid.external.shared.annotation.StateTransition;
import be.smals.research.bulksign.desktopapp.eid.external.shared.protocol.ProtocolState;

/**
 * Files digest request message transfer object.
 * 
 * @author Frank Cornelis
 * 
 */
@StateTransition(ProtocolState.DIGEST)
public class FilesDigestRequestMessage extends AbstractProtocolMessage {
	@HttpHeader(TYPE_HTTP_HEADER)
	@MessageDiscriminator
	public static final String TYPE = FilesDigestRequestMessage.class.getSimpleName();

	@HttpHeader(HTTP_HEADER_PREFIX + "DigestAlgo")
	@NotNull
	public String digestAlgo;
}
