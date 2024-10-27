import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate } from 'react-jhipster';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/project-owner">
        <Translate contentKey="global.menu.entities.projectOwner" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/project-reward">
        <Translate contentKey="global.menu.entities.projectReward" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/investment">
        <Translate contentKey="global.menu.entities.investment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/crowdfunding-project">
        <Translate contentKey="global.menu.entities.crowdfundingProject" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
