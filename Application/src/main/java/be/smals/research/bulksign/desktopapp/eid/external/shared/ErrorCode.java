/*
 * eID Applet Project.
 * Copyright (C) 2010 FedICT.
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

/**
 * Error enumeration.
 * 
 * @author Frank Cornelis
 * 
 */
public enum ErrorCode {

	/**
	 * Error code for expired certificates.
	 */
	CERTIFICATE_EXPIRED,

	/**
	 * Error code for revoked certificates.
	 */
	CERTIFICATE_REVOKED,

	/**
	 * Generic error code for invalid certificates.
	 */
	CERTIFICATE,

	/**
	 * Error code for untrusted certificates.
	 */
	CERTIFICATE_NOT_TRUSTED,

	/**
	 * User cancelled the eID operation.
	 */
	USER_CANCELED,

	/**
	 * User was not authorized to perform the requested operation.
	 */
	AUTHORIZATION;
}
