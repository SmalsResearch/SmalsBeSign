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

package be.smals.research.bulksign.desktopapp.eid.external.shared;

import be.smals.research.bulksign.desktopapp.eid.external.shared.annotation.HttpHeader;
import be.smals.research.bulksign.desktopapp.eid.external.shared.annotation.ProtocolVersion;

/**
 * Abstract protocol message class.
 * 
 * @author Frank Cornelis
 * 
 */
public abstract class AbstractProtocolMessage {

	public static final String HTTP_HEADER_PREFIX = "X-AppletProtocol-";

	public static final int PROTOCOL_VERSION = 1;

	@HttpHeader(HTTP_HEADER_PREFIX + "Version")
	@ProtocolVersion
	public static final int protocolVersion = PROTOCOL_VERSION;

	public static final String TYPE_HTTP_HEADER = HTTP_HEADER_PREFIX + "Type";
}
