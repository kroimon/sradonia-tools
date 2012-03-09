/*******************************************************************************
 * sradonia tools
 * Copyright (C) 2012 Stefan Rado
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/
package net.sradonia.servers.tcpserver;

import java.io.IOException;

/**
 * @author Stefan Rado
 */
public class TcpServerAdapter implements TcpServerListener {

	@Override
	public void onConnect(TcpServer src, TcpServerSocket connection) {
	}

	@Override
	public void onDisconnect(TcpServer src, TcpServerSocket connection) {
	}

	@Override
	public void onIOException(TcpServer src, TcpServerSocket connection, IOException e, String info) {
	}

	@Override
	public void onServerStarted(TcpServer src) {
	}

	@Override
	public void onServerStopped(TcpServer src) {
	}

}
