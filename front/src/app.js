export class App {


  configureRouter(config, router) {

    config.title = 'Anti-Monitor';
    config.options.pushState = true;
    config.options.root = '/';
    config.map([
      {
        route: ['', 'welcome'],
        name: 'welcome',
        moduleId: 'pages/welcome/welcome',
        nav: true,
        title: 'Accueil',
        menu: true
      },
      {route: 'projects', name: 'projects', moduleId: 'pages/projects', nav: true, title: 'Projets', menu: true},
      {route: 'tables', name: 'tables', moduleId: 'pages/tables', nav: true, title: 'Tables', menu: true},
      {route: 'apis', name: 'apis', moduleId: 'pages/apis', nav: true, title: 'Apis', menu: true},
      {
        route: 'configuration',
        name: 'configuration',
        moduleId: 'pages/conf/configuration',
        nav: true,
        title: 'Admin.',
        menu: true
      },
      {
        route: 'projects/add',
        name: 'projects-add',
        moduleId: 'pages/projects/add',
        nav: true,
        title: 'Projets - Ajout'
      }, {
        route: 'projects/:id',
        href: 'projects/:id',
        name: 'projects-detail',
        moduleId: 'pages/projects/detail',
        nav: true,
        title: 'Projets - Détail'
      },
      {route: 'about', name: 'about', moduleId: 'pages/about', nav: true, title: 'About', menu: true},
    ]);

    this.router = router;
  }
}
