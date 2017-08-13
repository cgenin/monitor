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
      {
        route: 'projects/add',
        name: 'projects-add',
        moduleId: 'pages/projects/add',
        nav: true,
        title: 'Projets - Ajout'
      },
      {route: 'about', name: 'about', moduleId: 'pages/about', nav: true, title: 'About', menu: true},
    ]);

    this.router = router;
  }
}
