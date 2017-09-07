/*
 * Copyright (C) 2015 David Rizo Valero
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
 */
package es.ua.dlsi.im3.midrepresentations;

import es.ua.dlsi.im3.core.IM3Exception;

import java.util.List;

/**
 *
 * @author drizo
 * @param <InputType> Usually PlayedSong, a SongTrack or a ScoreVoice
 * @param <MidLevelRepresentationType>
 */
public interface IMidLevelRepresentationEncoder<InputType, MidLevelRepresentationType extends IMidLevelRepresentation> {
    public MidLevelRepresentationType encode(InputType input) throws IM3Exception;
    /**
     * 
     * @param input
     * @param windowSize Number of bars
     * @param windowStep
     * @return
     * @throws IM3Exception
     */
    public List<MidLevelRepresentationType> encode(InputType input, int windowSize, int windowStep) throws IM3Exception;
}
