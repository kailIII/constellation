/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2005, Institut de Recherche pour le Développement
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.sicade.observation.coverage;

import net.sicade.observation.Element;
import net.sicade.observation.Observation;


/**
 * Position spatio-temporelle relative à une {@linkplain Observation observation}.
 * Il s'agit du décalage entre une observation (pêche, <cite>etc.</cite>) et un paramètre
 * environnemental (température, <cite>etc.</cite>) utilisé pour décrire l'environnement
 * de cette observation. Un décalage spatio-temporelle est une des trois composantes du
 * {@linkplain Descriptor descripteur du paysage océanique}.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 * @author Antoine Hnawia
 */
public interface LocationOffset extends Element {
    /**
     * Retourne le décalage temporelle, en nombre de jours. Par exemple la valeur -2 indique que
     * l'on s'intéressera à la valeur d'un paramètre environnemental (par exemple la température)
     * deux jours avant l'observation (par exemple une pêche).
     */
    double getDayOffset();

    /**
     * Retourne le décalage vers l'est, en mètres. Par exemple la valeur 1000 indique que l'on
     * s'intéresse à la valeur d'un paramètre environnemental 1 kilomètre à l'est de l'observation.
     */
    double getEasting();

    /**
     * Retourne le décalage vers le nord, en mètres. Par exemple la valeur 1000 indique que l'on
     * s'intéresse à la valeur d'un paramètre environnemental 1 kilomètre au nord de l'observation.
     */
    double getNorthing();

    /**
     * Retourne le décalage en altitude, en mètres. Par exemple la valeur 100 indique que l'on
     * s'intéresse à la valeur d'un paramètre environnemental 100 mètres sous l'observation (par
     * exemple sous la surface de la mer lorsque l'observation est une pêche de surface telle que
     * la pêche à la senne).
     */
    double getAltitudeOffset();
}
