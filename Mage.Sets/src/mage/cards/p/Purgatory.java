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
package mage.cards.p;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.costs.CompositeCost;
import mage.abilities.costs.common.PayLifeCost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DoIfCostPaid;
import mage.cards.Card;
import mage.cards.Cards;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.cards.CardsImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.game.ExileZone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.GameEvent.EventType;
import mage.game.events.ZoneChangeEvent;
import mage.game.permanent.Permanent;
import mage.game.permanent.PermanentToken;
import mage.players.Player;
import mage.target.TargetCard;
import mage.target.targetpointer.FixedTarget;

/**
 * 
 * @author L_J
 */
public class Purgatory extends CardImpl {

    public Purgatory(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{2}{W}{B}");

        // Whenever a nontoken creature is put into your graveyard from the battlefield, exile that card.
        this.addAbility(new PurgatoryTriggeredAbility());

        // At the beginning of your upkeep, you may pay {4} and 2 life. If you do, return a card exiled with Purgatory to the battlefield.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(Zone.BATTLEFIELD, 
            new DoIfCostPaid(new PurgatoryReturnEffect(), 
            new CompositeCost(new GenericManaCost(4), new PayLifeCost(2), "{4} and 2 life")),
            TargetController.YOU, 
            false));
    }

    public Purgatory(final Purgatory card) {
        super(card);
    }

    @Override
    public Purgatory copy() {
        return new Purgatory(this);
    }
}

class PurgatoryTriggeredAbility extends TriggeredAbilityImpl {

    PurgatoryTriggeredAbility() {
        super(Zone.BATTLEFIELD, new PurgatoryExileEffect(), false);
    }

    PurgatoryTriggeredAbility(PurgatoryTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public PurgatoryTriggeredAbility copy() {
        return new PurgatoryTriggeredAbility(this);
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == EventType.ZONE_CHANGE;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        Permanent sourcePermanent = game.getPermanentOrLKIBattlefield(getSourceId());
        if (sourcePermanent != null) {
            Player controller = game.getPlayer(sourcePermanent.getControllerId());
            if (controller != null) {
                ZoneChangeEvent zEvent = (ZoneChangeEvent) event;
                Permanent permanent = zEvent.getTarget();
                if (permanent != null
                        && zEvent.getToZone() == Zone.GRAVEYARD
                        && zEvent.getFromZone() == Zone.BATTLEFIELD
                        && !(permanent instanceof PermanentToken)
                        && permanent.isCreature()
                        && permanent.getOwnerId().equals(controller.getId())) {
        
                    this.getEffects().get(0).setTargetPointer(new FixedTarget(permanent.getId()));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever a nontoken creature is put into your graveyard from the battlefield, exile that card.";
    }
}

class PurgatoryExileEffect extends OneShotEffect {

    public PurgatoryExileEffect() {
        super(Outcome.Benefit);
        staticText = "exile that card";
    }

    public PurgatoryExileEffect(PurgatoryExileEffect effect) {
        super(effect);
    }
    
    
    @Override
    public boolean apply(Game game, Ability source) {
        Player sourceController = game.getPlayer(source.getControllerId());
        MageObject sourceObject = source.getSourceObject(game);
        Permanent permanent = game.getPermanentOrLKIBattlefield(this.getTargetPointer().getFirst(game, source));
        if (sourceController != null && sourceObject != null && permanent != null) {
            if (permanent.getZoneChangeCounter(game) + 1 == game.getState().getZoneChangeCounter(permanent.getId())
                    && !game.getState().getZone(permanent.getId()).equals(Zone.GRAVEYARD)) {
                // A replacement effect has moved the card to another zone as graveyard
                return true;
            }
            Player targetController = game.getPlayer(permanent.getControllerId());
            Card card = game.getCard(permanent.getId());
            if (targetController != null && card != null) {
                UUID exileId = (UUID) game.getState().getValue("SourceExileZone_" + source.getSourceId() + '_' + targetController.getName());
                if (exileId == null) {
                    exileId = UUID.randomUUID();
                    game.getState().setValue("SourceExileZone_" + source.getSourceId() + '_' + targetController.getName(), exileId);
                }
                sourceController.moveCardsToExile(card, source, game, true, exileId, sourceObject.getIdName() + " (" + targetController.getName() + ')');
                game.applyEffects();
                return true;
            }
        }
        return false;
    }

    @Override
    public PurgatoryExileEffect copy() {
        return new PurgatoryExileEffect(this);
    }

}

class PurgatoryReturnEffect extends OneShotEffect {

    public PurgatoryReturnEffect() {
        super(Outcome.PutCreatureInPlay);
        this.staticText = "return a card exiled with {this} to the battlefield";
    }

    public PurgatoryReturnEffect(final PurgatoryReturnEffect effect) {
        super(effect);
    }

    @Override
    public PurgatoryReturnEffect copy() {
        return new PurgatoryReturnEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanentOrLKIBattlefield(source.getSourceId());
        Player controller = game.getPlayer(source.getControllerId());
        MageObject sourceObject = source.getSourceObject(game);
        if (permanent != null && controller != null && sourceObject != null) {
            Set<ExileZone> exileZones = new HashSet<>();
            for (UUID playerId : game.getState().getPlayerList()) {
                Player player = game.getPlayer(playerId);
                if (player != null) {
                    UUID exileId = (UUID) game.getState().getValue("SourceExileZone_" + source.getSourceId() + '_' + player.getName());
                    if (exileId != null) {
                        ExileZone exileZone = game.getExile().getExileZone(exileId);
                        if (exileZone != null) {
                            exileZones.add(exileZone);
                        }
                    }
                }
            }
            if (!exileZones.isEmpty()) {
                Map<Card, ExileZone> cardsMap = new HashMap<>();
                for (ExileZone exileZone : exileZones) {
                    for (UUID cardId : exileZone) {
                        Card card = game.getCard(cardId);
                        if (card != null) {
                            cardsMap.put(card, exileZone);
                        }
                    }
                }
                if (!cardsMap.isEmpty()) {
                    Cards cards = new CardsImpl();
                    cards.addAll(cardsMap.keySet());
                    TargetCard targetCard = new TargetCard(Zone.EXILED, new FilterCard());
                    controller.chooseTarget(outcome, cards, targetCard, source, game);
                    Card card = game.getCard(targetCard.getFirstTarget());
                    if (card != null) {
                        ExileZone exileZone = cardsMap.get(card);
                        if (exileZone != null) {
                            String exileZoneName = exileZone.getName();
                            for (UUID playerId : game.getState().getPlayerList()) {
                                Player player = game.getPlayer(playerId);
                                if (player != null && exileZoneName.contains("(" + player.getName() + ')')) {
                                    player.moveCards(card, Zone.BATTLEFIELD, source, game);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}
