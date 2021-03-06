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
package mage.cards.u;

import java.util.UUID;
import mage.ConditionalMana;
import mage.MageObject;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.common.AsEntersBattlefieldAbility;
import mage.abilities.costs.Cost;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.effects.common.ChooseCreatureTypeEffect;
import mage.abilities.mana.ColorlessManaAbility;
import mage.abilities.mana.ConditionalAnyColorManaAbility;
import mage.abilities.mana.builder.ConditionalManaBuilder;
import mage.abilities.mana.conditional.CreatureCastManaCondition;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.game.Game;
import mage.players.Player;

/**
 *
 * @author spjspj
 */
public class UnclaimedTerritory extends CardImpl {

    public UnclaimedTerritory(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.LAND}, "");

        // As Unclaimed Territory enters the battlefield, choose a creature type.
        this.addAbility(new AsEntersBattlefieldAbility(new ChooseCreatureTypeEffect(Outcome.Benefit)));

        // {T}: Add {C} to your mana pool.
        this.addAbility(new ColorlessManaAbility());

        // {T}: Add one mana of any color to your mana pool. Spend this mana only to cast a creature spell of the chosen type.
        this.addAbility(new ConditionalAnyColorManaAbility(new TapSourceCost(), 1, new UnclaimedTerritoryManaBuilder(), true));
    }

    public UnclaimedTerritory(final UnclaimedTerritory card) {
        super(card);
    }

    @Override
    public UnclaimedTerritory copy() {
        return new UnclaimedTerritory(this);
    }
}

class UnclaimedTerritoryManaBuilder extends ConditionalManaBuilder {

    SubType creatureType;

    @Override
    public ConditionalManaBuilder setMana(Mana mana, Ability source, Game game) {
        SubType subType = ChooseCreatureTypeEffect.getChoosenCreatureType(source.getSourceId(), game);
        if (subType != null) {
            creatureType = subType;
        }
        Player controller = game.getPlayer(source.getControllerId());
        MageObject sourceObject = game.getObject(source.getSourceId());
        if (controller != null && sourceObject != null) {
            game.informPlayers(controller.getLogName() + " produces " + mana.toString() + " with " + sourceObject.getLogName()
                    + " (can only be spent to cast creatures of type " + creatureType + ")");
        }
        return super.setMana(mana, source, game);
    }

    @Override
    public ConditionalMana build(Object... options) {
        return new UnclaimedTerritoryConditionalMana(this.mana, creatureType);
    }

    @Override
    public String getRule() {
        return "Spend this mana only to cast a creature spell of the chosen type";
    }
}

class UnclaimedTerritoryConditionalMana extends ConditionalMana {

    public UnclaimedTerritoryConditionalMana(Mana mana, SubType creatureType) {
        super(mana);
        staticText = "Spend this mana only to cast a creature spell of the chosen type";
        addCondition(new UnclaimedTerritoryManaCondition(creatureType));
    }
}

class UnclaimedTerritoryManaCondition extends CreatureCastManaCondition {

    SubType creatureType;

    UnclaimedTerritoryManaCondition(SubType creatureType) {
        this.creatureType = creatureType;
    }

    @Override
    public boolean apply(Game game, Ability source, UUID originalId, Cost costToPay) {
        // check: ... to cast a creature spell
        if (super.apply(game, source)) {
            // check: ... of the chosen type
            MageObject object = game.getObject(source.getSourceId());
            if (creatureType != null && object.hasSubtype(creatureType, game)) {
                return true;
            }
        }
        return false;
    }
}
