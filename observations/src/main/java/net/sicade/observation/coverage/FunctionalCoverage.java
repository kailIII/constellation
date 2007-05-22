/*
 * Sicade - Syst�mes int�gr�s de connaissances pour l'aide � la d�cision en environnement
 * (C) 2006, Institut de Recherche pour le D�veloppement
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
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sicade.observation.coverage;

// J2SE dependencies
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.GregorianCalendar;

// OpenGIS dependencies
import org.opengis.coverage.Coverage;
import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.CannotEvaluateException;
import org.opengis.geometry.DirectPosition;

// Geotools dependencies
import org.geotools.resources.CRSUtilities;
import org.geotools.coverage.AbstractCoverage;
import org.geotools.referencing.crs.DefaultTemporalCRS;

// Sicade dependencies
import net.sicade.observation.sql.CRS;

// Static imports
import static java.lang.Math.*;
import static java.lang.Double.NaN;
import static java.lang.Double.isNaN;
import static java.lang.Double.isInfinite;


/**
 * Une couverture � une seule bande dont les valeurs sont calcul�es par une fonction plut�t que
 * d�termin�e � partir de donn�es. Le syst�me de r�f�rence des coordonn�es est fix� � celui de
 * {@link CRS#XYT}.
 * <p>
 * Un ensemble de couvertures sont pr�-d�finies pour des descripteurs tels que {@code cos(t)}.
 * Ces couvertures peuvent �tre obtenues par un appel � {@link #getCoverage(String)}.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
public abstract class FunctionalCoverage extends AbstractCoverage implements DynamicCoverage {
    /**
     * For compatibility during cross-version serialization.
     */
    private static final long serialVersionUID = 3894665589165786383L;

    /**
     * Ensemble des couvertures pr�d�finies.
     */
    private static final Map<String,DynamicCoverage> COVERAGES = new HashMap<String,DynamicCoverage>();

    /**
     * Ajoute � {@link #COVERAGES} des couvertures pr�-d�finies.
     * L'ajout est effectu� par le constructeur de {@link FunctionalCoverage}.
     */
    static {
        new Identity();
        new Longitude();
        new Latitude();
        new SinusLatitude();
        new CosinusLatitude();
        new CosinusTime();
    }

    /**
     * Construit une nouvelle instance pour le nom sp�cifi�. L'instante construite sera
     * automatiquement ajout�e � l'ensemble des {@linkplain #getCoverage couvertures pr�-d�finies}.
     *
     * @param  name Le nom de la nouvelle couverture.
     * @param  crs Son syst�me de r�f�rence des coordonn�es.
     * @throws IllegalArgumentException si une couverture �tait d�j� enregistr�e pour le nom sp�cifi�.
     */
    protected FunctionalCoverage(final String name) throws IllegalArgumentException {
        super(name, CRS.XYT.getCoordinateReferenceSystem(), null, null);
        synchronized (COVERAGES) {
            final DynamicCoverage old = COVERAGES.put(name, this);
            if (old != null) {
                COVERAGES.put(name, old);
                throw new IllegalArgumentException(name);
            }
        }
    }

    /**
     * Retourne une couverture pour le nom sp�cifi�, ou {@code null} s'il n'y en a pas.
     */
    public static DynamicCoverage getCoverage(final String name) {
        synchronized (COVERAGES) {
            return COVERAGES.get(name);
        }
    }

    /**
     * Retourne la valeur de la fonction pour la position sp�cifi�e.
     *
     * @param  coord La coordonn�es � laquelle �valuer cette fonction.
     * @throws CannotEvaluateException si la valeur ne peut pas �tre calcul�e.
     */
    protected abstract double compute(final DirectPosition coord) throws CannotEvaluateException;

    /**
     * Retourne la valeur de la fonction pour la position sp�cifi�e, sous forme
     * d'un tableau de type {@code double[]} de longueur 1.
     */
    public final double[] evaluate(final DirectPosition coord) {
        return new double[] {compute(coord)};
    }

    /**
     * Retourne la valeur de la fonction pour la position sp�cifi�e, sous forme
     * d'un tableau de type {@code double[]} de longueur 1.
     */
    @Override
    public final double[] evaluate(final DirectPosition coord, double[] dest) {
        if (dest == null) {
            dest = new double[1];
        }
        dest[0] = compute(coord);
        return dest;
    }

    /**
     * Retourne la valeur de la fonction pour la position sp�cifi�e, sous forme
     * d'un tableau de type {@code float[]} de longueur 1.
     */
    @Override
    public final float[] evaluate(final DirectPosition coord, float[] dest) {
        if (dest == null) {
            dest = new float[1];
        }
        dest[0] = (float) compute(coord);
        return dest;
    }

    /**
     * Retourne la valeur de la fonction pour la position sp�cifi�e, sous forme
     * d'un tableau de type {@code int[]} de longueur 1.
     */
    @Override
    public final int[] evaluate(final DirectPosition coord, int[] dest) {
        if (dest == null) {
            dest = new int[1];
        }
        dest[0] = (int) max(Integer.MIN_VALUE, min(Integer.MAX_VALUE, rint(compute(coord))));
        return dest;
    }

    /**
     * Retourne la valeur de la fonction pour la position sp�cifi�e, sous forme
     * d'un tableau de type {@code byte[]} de longueur 1.
     */
    @Override
    public final byte[] evaluate(final DirectPosition coord, byte[] dest) {
        if (dest == null) {
            dest = new byte[1];
        }
        dest[0] = (byte) max(Byte.MIN_VALUE, min(Byte.MAX_VALUE, rint(compute(coord))));
        return dest;
    }

    /**
     * Retourne la valeur de la fonction pour la position sp�cifi�e, sous forme
     * d'un tableau de type {@code boolean[]} de longueur 1.
     */
    @Override
    public final boolean[] evaluate(final DirectPosition coord, boolean[] dest) {
        if (dest == null) {
            dest = new boolean[1];
        }
        final double v = compute(coord);
        dest[0] = !Double.isNaN(v) && v!=0;
        return dest;
    }

    /**
     * Retourne le nombre de dimensions, qui sera toujours 1.
     */
    public final int getNumSampleDimensions() {
        return 1;
    }

    /**
     * Retourne une description de la bande.
     *
     * @todo Pas encore impl�ment�e.
     */
    public final SampleDimension getSampleDimension(final int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * Retourne la position sp�cifi�e inchang�e.
     */
    public final DirectPosition snap(final DirectPosition position) {
        return position;
    }

    /**
     * Retourne un ensemble toujours vide.
     */
    public final List<Coverage> coveragesAt(final double t) {
        return Collections.emptyList();
    }

    /**
     * La couverture identity.
     */
    private static final class Identity extends FunctionalCoverage {
        public Identity() {
            super("\u2460");
        }
        protected double compute(final DirectPosition coord) {
            return 1;
        }
    }

    /**
     * Une couverture retournant la longitude.
     */
    private static final class Longitude extends FunctionalCoverage {
        public Longitude() {
            super("\u03BB");
        }
        protected double compute(final DirectPosition coord) {
            return coord.getOrdinate(0);
        }
    }

    /**
     * Une couverture retournant la latitude.
     */
    private static final class Latitude extends FunctionalCoverage {
        public Latitude() {
            super("\u03C6");
        }
        protected double compute(final DirectPosition coord) {
            return coord.getOrdinate(1);
        }
    }

    /**
     * Une couverture retournant le sinus de la latitude.
     */
    private static final class SinusLatitude extends FunctionalCoverage {
        public SinusLatitude() {
            super("sin(\u03C6)");
        }
        protected double compute(final DirectPosition coord) {
            return sin(toRadians(coord.getOrdinate(1)));
        }
    }

    /**
     * Une couverture retournant le cosinus de la latitude.
     */
    private static final class CosinusLatitude extends FunctionalCoverage {
        public CosinusLatitude() {
            super("cos(\u03C6)");
        }
        protected double compute(final DirectPosition coord) {
            return cos(toRadians(coord.getOrdinate(1)));
        }
    }

    /**
     * Une couverture retournant le cosinus de la date. Cette couverture simule le calcul
     * {@code EXTRACT(DOY FROM TIMESTAMP ...)} de PostgreSQL, ou {@code DOY} signifie
     * <cite>Day Of Year</cite> et est une valeur entre 1 et (365 ou 366) inclusivement.
     * Le facteur multiplicatif ({@code PI / 182.625}) doit �tre identique � celui qui
     * est utilis� dans la requ�te {@code "AllEnvironments"}.
     */
    private static final class CosinusTime extends FunctionalCoverage {
        /**
         * Le syst�me de r�f�rence des coordonn�es pour l'axe du temps.
         */
        private final DefaultTemporalCRS crs;

        /**
         * Le calendrier � utiliser pour manipuler les dates.
         */
        private final Calendar calendar;

        /**
         * Construit une converture pour le descripteur {@code cos(t)}.
         */
        public CosinusTime() {
            super("cos(t)");
            crs = DefaultTemporalCRS.wrap(CRSUtilities.getTemporalCRS(getCoordinateReferenceSystem()));
            calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.CANADA);
        }

        /**
         * Calcule la valeur de {@code cos(t)}.
         */
        protected synchronized double compute(final DirectPosition coord) {
            double t = coord.getOrdinate(2);
            if (isNaN(t) || isInfinite(t)) {
                return NaN;
            }
            calendar.setTime(crs.toDate(t));
            t = (((((calendar.get(Calendar.MILLISECOND)) / 1000.0 +
                     calendar.get(Calendar.SECOND     )) /   60.0 +
                     calendar.get(Calendar.MINUTE     )) /   60.0 +
                     calendar.get(Calendar.HOUR_OF_DAY)) /   24.0 +
                     calendar.get(Calendar.DAY_OF_YEAR));
            return cos(t * (PI / 182.625));
        }
    }
}
