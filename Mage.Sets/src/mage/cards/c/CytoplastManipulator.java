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
package mage.cards.c;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.condition.common.SourceOnBattlefieldCondition;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.decorator.ConditionalContinuousEffect;
import mage.abilities.effects.common.continuous.GainControlTargetEffect;
import mage.abilities.keyword.GraftAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.Duration;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.permanent.CounterPredicate;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author JotaPeRL
 */
public class CytoplastManipulator extends CardImpl {
    
    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("a creature with a +1/+1 counter on it");
    static {
        filter.add(new CounterPredicate(CounterType.P1P1));
    }    

    public CytoplastManipulator(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{2}{U}{U}");
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.WIZARD);
        this.subtype.add(SubType.MUTANT);
        this.power = new MageInt(0);
        this.toughness = new MageInt(0);

        // Graft 2
        this.addAbility(new GraftAbility(this, 2));
        
        // {U}, {tap}: Gain control of target creature with a +1/+1 counter on it for as long as Cytoplast Manipulator remains on the battlefield.
        ConditionalContinuousEffect effect = new ConditionalContinuousEffect(
                new GainControlTargetEffect(Duration.Custom, true),
                SourceOnBattlefieldCondition.instance,
                "gain control of target creature with a +1/+1 counter on it for as long as {this} remains on the battlefield");
        Ability ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, effect, new ManaCostsImpl("{U}"));
        ability.addCost(new TapSourceCost());
        ability.addTarget(new TargetCreaturePermanent(filter));
        this.addAbility(ability);
        
    }

    public CytoplastManipulator(final CytoplastManipulator card) {
        super(card);
    }

    @Override
    public CytoplastManipulator copy() {
        return new CytoplastManipulator(this);
    }
}
