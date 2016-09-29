/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.sets.starwars;

import java.util.List;
import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.filter.common.FilterCreatureCard;
import mage.game.Game;
import mage.players.Player;
import mage.target.common.TargetCardInYourGraveyard;

/**
 *
 * @author Styxo
 */
public class ImagesOfThePast extends CardImpl {

    public ImagesOfThePast(UUID ownerId) {
        super(ownerId, 176, "Images of the Past", Rarity.COMMON, new CardType[]{CardType.INSTANT}, "{G}{W}");
        this.expansionSetCode = "SWS";

        // Return up to two target creature cards from your graveyard to the battlefield, then exile those creatures.
        this.getSpellAbility().addEffect(new ImagesOfThePastEffect());
        this.getSpellAbility().addTarget(new TargetCardInYourGraveyard(0, 2, new FilterCreatureCard("creature cards from your graveyard")));

    }

    public ImagesOfThePast(final ImagesOfThePast card) {
        super(card);
    }

    @Override
    public ImagesOfThePast copy() {
        return new ImagesOfThePast(this);
    }
}

class ImagesOfThePastEffect extends OneShotEffect {

    ImagesOfThePastEffect() {
        super(Outcome.PutCreatureInPlay);
        this.staticText = "Return up to two target creature cards from your graveyard to the battlefield, then exile those creatures";
    }

    ImagesOfThePastEffect(final ImagesOfThePastEffect effect) {
        super(effect);
    }

    @Override
    public ImagesOfThePastEffect copy() {
        return new ImagesOfThePastEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        if (player != null) {
            List<UUID> targets = source.getTargets().get(0).getTargets();
            for (UUID targetId : targets) {
                Card card = game.getCard(targetId);
                if (card != null) {
                    player.moveCards(card, Zone.BATTLEFIELD, source, game);
                    player.moveCards(card, Zone.EXILED, source, game);
                }
            }
            return true;
        }
        return false;
    }
}