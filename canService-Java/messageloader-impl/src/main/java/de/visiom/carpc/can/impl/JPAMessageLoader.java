/*
 * Copyright 2015 Technische Universität München
 *
 * Author:
 * David Soto Setzke
 *
 *
 * This file is part of the Automotive Service Bus v1.1 which was
 * forked from the research project Visio.M:
 *
 * 	 http://www.visiom-automobile.de/home/
 *
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * 	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package de.visiom.carpc.can.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import de.visiom.carpc.can.MessageLoader;
import de.visiom.carpc.can.entities.Message;
import de.visiom.carpc.can.impl.fixture.DatabaseFixture;

public class JPAMessageLoader implements MessageLoader {

    private EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactory(
            EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.visiom.carpc.can.MessageLoader#loadMessages()
     */
    @Override
    public synchronized List<Message> loadMessages() {
        List<Message> result = loadFromDatabase();
        if (result.size() == 0) {
            new DatabaseFixture().fillDatabase(entityManagerFactory);
            result = loadFromDatabase();
        }
        return result;
    }

    private List<Message> loadFromDatabase() {
        synchronized (this) {
            EntityManager entityManager = entityManagerFactory
                    .createEntityManager();
            TypedQuery<Message> q = entityManager.createQuery(
                    "select m from Message m", Message.class);
            return q.getResultList();
        }
    }
}
