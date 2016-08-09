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

package be.smals.research.bulksign.desktopapp.eid.external.io;

import be.smals.research.bulksign.desktopapp.eid.external.View;
import be.smals.research.bulksign.desktopapp.eid.external.shared.protocol.ProtocolContext;
import be.smals.research.bulksign.desktopapp.eid.external.shared.protocol.ProtocolState;

/**
 * Local memory protocol context implementation.
 * 
 * @author Frank Cornelis
 * 
 */
public class LocalAppletProtocolContext implements ProtocolContext {

	private final View view;

	/**
	 * Main constructor.
	 * 
	 * @param view
	 */
	public LocalAppletProtocolContext(View view) {
		this.view = view;
	}

	private ProtocolState protocolState;

	@Override
	public ProtocolState getProtocolState() {
		this.view.addDetailMessage("current protocol state: " + this.protocolState);
		return this.protocolState;
	}

	@Override
	public void removeProtocolState() {
		this.view.addDetailMessage("removing protocol state");
		this.protocolState = null;
	}

	@Override
	public void setProtocolState(ProtocolState protocolState) {
		this.view.addDetailMessage("protocol state transition: " + protocolState);
		this.protocolState = protocolState;
	}
}
