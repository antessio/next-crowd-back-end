import React from 'react';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/crowdfunding-project-owner">
        <Translate contentKey="global.menu.entities.crowdfundingProjectOwner" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/crowdfunding-project">
        <Translate contentKey="global.menu.entities.crowdfundingProject" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/project-reward">
        <Translate contentKey="global.menu.entities.projectReward" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
