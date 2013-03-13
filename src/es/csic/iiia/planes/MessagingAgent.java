/*
 * Software License Agreement (BSD License)
 *
 * Copyright 2012 Marc Pujol <mpujol@iiia.csic.es>.
 *
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 *
 *   Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 *
 *   Neither the name of IIIA-CSIC, Artificial Intelligence Research Institute
 *   nor the names of its contributors may be used to
 *   endorse or promote products derived from this
 *   software without specific prior written permission of
 *   IIIA-CSIC, Artificial Intelligence Research Institute
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package es.csic.iiia.planes;

import es.csic.iiia.planes.Agent;
import es.csic.iiia.planes.Positioned;
import es.csic.iiia.planes.messaging.Message;

/**
 * An {@link Agent} that communicates with other agents using message passing.
 *
 * @author Marc Pujol <mpujol@iiia.csic.es>
 */
public interface MessagingAgent extends Agent, Positioned, Comparable {

    /**
     * Get the communication range of this agent.
     *
     * The communication range of an agent defines the furthest distance (in
     * meters) at which it is able to send messages.
     *
     * @return communication range of this agent.
     */
    public double getCommunicationRange();

    /**
     * Set the communication range of this agent.
     *
     * @see #getCommunicationRange()
     * @param range communication range.
     */
    public void setCommunicationRange(double range);

    /**
     * Send a message.
     */
    public void send(Message message);

    /**
     * Receive a message issued by another agent.
     *
     * Since this is a synchronous platform, the messages must be stored
     * so that they are <strong>not</strong> available to the agent
     * until at least the next iteration (tenth of second).
     */
    public void receive(Message message);

}